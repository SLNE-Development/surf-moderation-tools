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
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqUsedEvent
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqsRequest
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqsResponse
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.redis.RedisService
import dev.slne.surf.moderation.tools.util.appendArtyPrefix
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.milliseconds

object FaqService {
    private val log = logger()
    private val reloadMutex = Mutex()

    private val faqCooldown = SurfModerationToolConfig.getConfig().faqCooldown
    private val lastFaqUsage = Caffeine.newBuilder()
        .expireAfterWrite(faqCooldown.milliseconds)
        .build<String, Long>()

    @Volatile
    private var cachedFaqs: Map<String, Faq> = emptyMap()

    fun all(): Collection<Faq> = cachedFaqs.values

    fun byKey(key: String): Faq? = cachedFaqs[key.lowercase()]

    suspend fun reload(): Int? = reloadMutex.withLock {
        try {
            val response = RedisService.redisApi.sendRequest<MinecraftFaqsResponse>(MinecraftFaqsRequest())

            cachedFaqs = response.faqs
                .map { Faq.fromData(it) }
                .sortedBy { it.key }
                .associateBy { it.key.lowercase() }

            log.atInfo().log("Loaded %d FAQs from the FAQ microservice.", cachedFaqs.size)
            cachedFaqs.size
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.atWarning()
                .withCause(e)
                .log("Failed to load FAQs from the FAQ microservice, keeping %d cached FAQs.", cachedFaqs.size)
            null
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