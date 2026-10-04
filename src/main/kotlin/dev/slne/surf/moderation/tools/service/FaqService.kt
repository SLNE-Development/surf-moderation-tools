package dev.slne.surf.moderation.tools.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqData
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqUsedEvent
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqsRequest
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqsResponse
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.redis.RedisService
import dev.slne.surf.moderation.tools.util.appendArtyPrefix
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.moveTo
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.milliseconds

object FaqService {
    private val log = logger()
    private val reloadMutex = Mutex()

    private val cacheFile = plugin.dataPath.resolve("faq-cache.json")
    private val cacheSerializer = ListSerializer(MinecraftFaqData.serializer())
    private val cacheJson = Json { prettyPrint = true }

    private val faqCooldown = SurfModerationToolConfig.getConfig().faqCooldown
    private val lastFaqUsage = Caffeine.newBuilder()
        .expireAfterWrite(faqCooldown.milliseconds)
        .build<String, Long>()

    @Volatile
    private var cachedFaqs: Map<String, Faq> = emptyMap()

    fun all(): Collection<Faq> = cachedFaqs.values

    fun byKey(key: String): Faq? = cachedFaqs[key.lowercase()]

    suspend fun reload(refresh: Boolean = false): Int? = reloadMutex.withLock {
        try {
            val response = RedisService.redisApi.sendRequest<MinecraftFaqsResponse>(MinecraftFaqsRequest(refresh))

            applyFaqs(response.faqs)
            log.atInfo().log("Loaded %d FAQs from the FAQ microservice.", cachedFaqs.size)

            writeCacheFile(response.faqs)
            cachedFaqs.size
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.atWarning()
                .withCause(e)
                .log("Failed to load FAQs from the FAQ microservice, keeping %d cached FAQs.", cachedFaqs.size)

            if (cachedFaqs.isEmpty()) {
                loadCacheFile()
            }
            null
        }
    }

    private fun applyFaqs(faqs: List<MinecraftFaqData>) {
        cachedFaqs = faqs
            .map { Faq.fromData(it) }
            .sortedBy { it.key }
            .associateBy { it.key.lowercase() }
    }

    private suspend fun writeCacheFile(faqs: List<MinecraftFaqData>) {
        try {
            withContext(Dispatchers.IO) {
                cacheFile.parent.createDirectories()

                val tempFile = cacheFile.resolveSibling("${cacheFile.fileName}.tmp")
                tempFile.writeText(cacheJson.encodeToString(cacheSerializer, faqs))
                tempFile.moveTo(cacheFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.atWarning()
                .withCause(e)
                .log("Failed to write the FAQ cache file %s.", cacheFile)
        }
    }

    private suspend fun loadCacheFile() {
        try {
            val faqs = withContext(Dispatchers.IO) {
                if (cacheFile.notExists()) return@withContext null
                cacheJson.decodeFromString(cacheSerializer, cacheFile.readText())
            }

            if (faqs == null) {
                log.atWarning().log("No FAQ cache file found, no FAQs are available until the FAQ microservice responds.")
                return
            }

            applyFaqs(faqs)
            log.atInfo().log("Loaded %d FAQs from the FAQ cache file.", cachedFaqs.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.atWarning()
                .withCause(e)
                .log("Failed to read the FAQ cache file %s.", cacheFile)
        }
    }

    private fun recordUsage(faq: Faq) {
        try {
            RedisService.redisApi.publishEvent(MinecraftFaqUsedEvent(faq.key))
        } catch (e: Exception) {
            log.atWarning()
                .withCause(e)
                .log("Failed to report usage of FAQ '%s' to the FAQ microservice.", faq.key)
        }
    }

    suspend fun sendFaq(
        executor: Player,
        faq: Faq,
        targets: Collection<Player>? = null,
        infoOnly: Boolean = false
    ) {
        if (infoOnly) {
            executor.sendText {
                appendArtyPrefix()
                append(faq)
            }
            return
        }

        val now = System.currentTimeMillis()
        val lastUsed = lastFaqUsage.getIfPresent(faq.key) ?: 0L
        val remainingMillis = faqCooldown - (now - lastUsed)

        if (remainingMillis > 0) {
            executor.sendText {
                appendErrorPrefix()
                error("Du musst noch ")
                variableValue(remainingMillis.milliseconds.toString())
                error(" warten, bevor du den Faq-Eintrag wieder verwenden kannst.")
            }
            return
        }

        lastFaqUsage.put(faq.key, now)
        recordUsage(faq)

        if (targets.isNullOrEmpty()) {
            server.sendText {
                appendArtyPrefix()
                append(faq)
            }
        } else {
            executor.sendText {
                appendSuccessPrefix()
                variableValue(faq.key)
                success(" wurde erfolgreich gesendet!")
            }
            supervisorScope {
                for (target in targets) {
                    launch(plugin.entityDispatcher(target)) {
                        target.sendText {
                            appendArtyPrefix()
                            variableValue("@${target.name}", TextDecoration.BOLD)
                            appendSpace()
                            append(faq)
                        }

                        target.playSound(useSelfEmitter = true) {
                            type(Sound.ENTITY_CHICKEN_EGG)
                            source(Source.MASTER)
                            volume(1f)
                            pitch(1f)
                        }
                    }
                }
            }
        }
    }
}