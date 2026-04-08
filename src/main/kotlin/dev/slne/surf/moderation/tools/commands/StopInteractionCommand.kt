package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import io.papermc.paper.dialog.Dialog
import org.bukkit.entity.Player

fun stopInteractionCommand() = commandAPICommand("stopInteraction") {
    entitySelectorArgumentOnePlayer("targetPlayer")
    withPermission(PermissionRegistry.COMMAND_STOP_INTERACTION)

    anyExecutorSuspend { sender, args ->
        val targetPlayer: Player by args

        targetPlayer.showDialog(Dialog.QUICK_ACTIONS)
        targetPlayer.closeDialog()

        sender.sendText {
            appendSuccessPrefix()
            variableValue(targetPlayer.name)
            success("`s Interaktion wurde unterbrochen.")
        }
    }
}