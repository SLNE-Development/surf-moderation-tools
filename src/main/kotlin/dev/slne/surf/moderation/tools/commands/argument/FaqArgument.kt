package dev.slne.surf.moderation.tools.commands.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.api.core.messages.adventure.buildText

class FaqArgument(nodeName: String) : CustomArgument<Faq, String>(
    StringArgument(nodeName),
    { info ->
        val normalizedInput = info.input.trim()

        Faq.byId(normalizedInput)
            ?: throw CustomArgumentException.fromAdventureComponent(
            buildText {
                appendErrorPrefix()
                error("Das FAQ")
                appendSpace()
                variableValue(normalizedInput)
                appendSpace()
                error("existiert nicht.")
            }
        )
    })
{
    init {
        replaceSuggestions(
            ArgumentSuggestions.strings {
                SurfModerationToolConfig.getConfig().faqs.map { it.id }.toTypedArray()
            }
        )
    }
}

inline fun CommandAPICommand.faqArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(FaqArgument(nodeName).setOptional(optional).apply(block))