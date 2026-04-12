package dev.slne.surf.moderation.tools.commands

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.util.random
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player

fun rotateCommand() = commandAPICommand("rotate") {
    entitySelectorArgumentOnePlayer("targetPlayer")
    withPermission(PermissionRegistry.COMMAND_ROTATE)

    anyExecutorSuspend { sender, args ->
        val targetPlayer: Player by args

        val randomYaw = random.nextFloat(-180f, 180f)
        val randomPitch = random.nextFloat(-90f, 90f)

        withContext(plugin.entityDispatcher(targetPlayer)) {
            targetPlayer.setRotation(randomYaw, randomPitch)

            sender.sendText {
                appendSuccessPrefix()
                variableValue(targetPlayer.name)
                success(" wurde erfolgreich rotiert.")
            }
        }
    }
}