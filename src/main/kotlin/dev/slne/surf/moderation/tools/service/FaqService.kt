package dev.slne.surf.moderation.tools.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.util.appendArtyPrefix
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.milliseconds

object FaqService {
    private val faqCooldown = SurfModerationToolConfig.getConfig().faqCooldown
    private val lastFaqUsage = Caffeine.newBuilder()
        .expireAfterWrite(faqCooldown.milliseconds)
        .build<Faq, Long>()

    suspend fun sendFaq(
        executor: Player,
        faq: Faq,
        targets: Collection<Player>? = null
    ) {
        if (!faq.enabled) {
            executor.sendText {
                appendErrorPrefix()
                error("Dieses FAQ ist deaktiviert und kann nicht gesendet werden.")
            }
            return
        }

        val now = System.currentTimeMillis()
        val lastUsed = lastFaqUsage.getIfPresent(faq) ?: 0L
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

        lastFaqUsage.put(faq, now)

        if (targets.isNullOrEmpty()) {
            server.sendText {
                appendArtyPrefix()
                append(faq)
            }
        } else {
            executor.sendText {
                appendSuccessPrefix()
                variableValue(faq.id)
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