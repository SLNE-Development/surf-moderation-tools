package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentManyPlayers
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.moderation.tools.commands.argument.faqArgument
import dev.slne.surf.moderation.tools.dialog.createFaqSettingsDialog
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.moderation.tools.service.FaqService
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import org.bukkit.entity.Player

fun faqCommand() = commandAPICommand("faq") {
    withPermission(PermissionRegistry.COMMAND_FAQ)

    subcommand("settings") {
        withPermission(PermissionRegistry.COMMAND_FAQ_SETTINGS)
        playerExecutorSuspend { player, _ ->
            player.showDialog(createFaqSettingsDialog())
        }
    }

    subcommand("send") {
        withPermission(PermissionRegistry.COMMAND_FAQ_SEND)
        faqArgument("faq")
        entitySelectorArgumentManyPlayers("targets", optional = true)
        playerExecutorSuspend { player, args ->
            val faq: Faq by args
            val targets: List<Player>? by args

            FaqService.sendFaq(player, faq, targets)
        }
    }
}