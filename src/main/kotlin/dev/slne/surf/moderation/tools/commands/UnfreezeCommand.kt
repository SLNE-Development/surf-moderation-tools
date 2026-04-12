package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.moderation.tools.service.FreezeService
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import org.bukkit.entity.Player

fun unfreezeCommand() = commandAPICommand("unfreeze") {
    entitySelectorArgumentOnePlayer("targetPlayer")
    withPermission(PermissionRegistry.COMMAND_UNFREEZE)

    anyExecutor { sender, args ->
        val targetPlayer: Player by args

        if (!FreezeService.isFrozen(targetPlayer.uniqueId)) {
            sender.sendText {
                appendErrorPrefix()
                variableValue(targetPlayer.name)
                error(" ist nicht eingefroren.")
            }
            return@anyExecutor
        }

        FreezeService.unfreeze(targetPlayer.uniqueId)

        sender.sendText {
            appendSuccessPrefix()
            variableValue(targetPlayer.name)
            success(" wurde erfolgreich aufgetaut.")
        }
    }
}