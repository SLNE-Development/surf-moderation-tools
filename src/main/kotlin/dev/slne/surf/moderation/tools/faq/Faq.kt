package dev.slne.surf.moderation.tools.faq

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.moderation.tools.faq.redis.MinecraftFaqData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextReplacementConfig

data class Faq(
    val key: String,
    val question: String,
    val shortText: String
) : ComponentLike {
    private val cachedComponent: Component = buildText { text(shortText) }.replaceText(linkReplacement)

    override fun asComponent(): Component = cachedComponent

    companion object {
        private val urlPattern = Regex("""https?://[^\s<>()]*[^\s<>().,;:!?]""").toPattern()

        private val linkReplacement = TextReplacementConfig.builder()
            .match(urlPattern)
            .replacement { match, _ ->
                val url = match.group()

                buildText {
                    append {
                        variableValue(url)
                        hoverEvent(buildText { spacer("Klicke, um den Link zu öffnen.") })
                        clickOpensUrl(url)
                    }
                }
            }
            .build()

        fun fromData(data: MinecraftFaqData) = Faq(data.key, data.question, data.shortText)
    }
}
