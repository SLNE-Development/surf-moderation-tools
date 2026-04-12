package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import dev.slne.surf.moderation.tools.util.durationArgument
import kotlin.time.Duration.Companion.milliseconds

fun CommandAPICommand.setMessageCooldownCommand() = subcommand("setMessageCooldown") {
    withPermission(PermissionRegistry.COMMAND_SURF_MOD_TOOLS)
    durationArgument("cooldown")

    anyExecutor { sender, args ->
        val cooldown: Long by args

        SurfModerationToolConfig.edit {
            faqCooldown = cooldown
        }

        sender.sendText {
            appendSuccessPrefix()
            success("Der Nachrichten Cooldown wurde auf")
            appendSpace()
            variableValue(cooldown.milliseconds.toString())
            appendSpace()
            success("gesetzt.")
        }
    }
}