package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfModToolsReloadCommand() = subcommand("reload") {
    withPermission(PermissionRegistry.COMMAND_SURF_MOD_TOOLS)
    anyExecutor { sender, _ ->
        val ms = measureTimeMillis {
            SurfModerationToolConfig.reloadFromFile()
        }

        sender.sendText {
            appendSuccessPrefix()
            success("Das Plugin wurde erfolgreich neu geladen ")
            spacer("({$ms}ms)")
        }
    }
}