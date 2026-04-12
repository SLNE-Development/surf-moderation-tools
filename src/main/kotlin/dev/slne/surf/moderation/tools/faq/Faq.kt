package dev.slne.surf.moderation.tools.faq

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.minimessage.MiniMessage.miniMessage
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class Faq(
    val id: String,
    val content: String,
    var enabled: Boolean = true
) : ComponentLike {
    private val cachedComponent: Component = miniMessage().deserialize(content)

    override fun asComponent(): Component = cachedComponent

    companion object {
        fun create(
            id: String,
            content: SurfComponentBuilder.() -> Unit,
            enabled: Boolean = true
        ): Faq =
            Faq(
                id,
                miniMessage().serialize(SurfComponentBuilder(content)),
                enabled
            )

        fun byId(id: String): Faq? {

            return SurfModerationToolConfig.getConfig().faqs.find {
                it.id.equals(id, ignoreCase = true)
            }
        }

        fun persist(faq: Faq, replace: Boolean = false) {
            SurfModerationToolConfig.edit {

                if (replace) {
                    faqs.removeIf { it.id == faq.id }
                }

                faqs.add(faq)
            }
            SurfModerationToolConfig.save()
        }

        fun update(faq: Faq) {

            SurfModerationToolConfig.edit {
                faqs.removeIf { it.id == faq.id }
            }
            SurfModerationToolConfig.save()
        }

        fun allFaqs(): List<Faq> = SurfModerationToolConfig.getConfig().faqs.toList() }
    }
