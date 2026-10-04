package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.service.FaqService
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import kotlin.time.measureTimedValue

fun CommandAPICommand.surfModToolsReloadCommand() = subcommand("reload") {
    withPermission(PermissionRegistry.COMMAND_SURF_MOD_TOOLS)
    anyExecutorSuspend { sender, _ ->
        val (faqCount, duration) = measureTimedValue {
            SurfModerationToolConfig.reloadFromFile()
            FaqService.reload()
        }

        sender.sendText {
            appendSuccessPrefix()
            success("Das Plugin wurde erfolgreich neu geladen ")
            spacer("(${duration.inWholeMilliseconds}ms)")
        }

        if (faqCount == null) {
            sender.sendText {
                appendErrorPrefix()
                error("Die FAQs konnten nicht vom FAQ-Service geladen werden, es werden weiterhin ")
                variableValue(FaqService.all().size)
                error(" zwischengespeicherte FAQs verwendet.")
            }
        } else {
            sender.sendText {
                appendSuccessPrefix()
                variableValue(faqCount)
                success(" FAQs wurden geladen.")
            }
        }
    }
}
