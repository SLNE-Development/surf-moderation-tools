package dev.slne.surf.moderation.tools.commands

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.jorel.commandapi.kotlindsl.getValue
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.service.FreezeService
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import dev.slne.surf.moderation.tools.util.durationArgument
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player


fun freezeCommand() = commandAPICommand("freeze") {
    withPermission(PermissionRegistry.COMMAND_FREEZE)
    entitySelectorArgumentOnePlayer("targetPlayer")
    durationArgument("duration")

    anyExecutorSuspend { sender, args ->
        val targetPlayer: Player by args
        val duration: Long by args

        val targetUuid = targetPlayer.uniqueId

        if (FreezeService.isFrozen(targetUuid)) {
            sender.sendText {
                appendInfoPrefix()
                variableValue(targetPlayer.name)
                appendSpace()
                error("ist bereits eingefroren.")
            }
            return@anyExecutorSuspend
        }

        FreezeService.freeze(targetUuid, duration)

        withContext(plugin.entityDispatcher(targetPlayer)) {
            val location = targetPlayer.location
            val world = location.world

            var found = false

            for (y in location.blockY downTo world.minHeight) {
                val block = world.getBlockAt(location.blockX, y, location.blockZ)

                if (!block.isPassable) {
                    val tpLocation = block.location.add(0.5, 1.0, 0.5).apply {
                        yaw = location.yaw
                        pitch = location.pitch
                    }

                    val success = targetPlayer.teleportAsync(tpLocation).await()
                    if (!success) {
                        targetPlayer.teleportAsync(world.spawnLocation).await()
                    }

                    found = true
                    break
                }
            }

            if (!found) {
                targetPlayer.teleportAsync(world.spawnLocation).await()
            }
        }

        sender.sendText {
            appendSuccessPrefix()
            variableValue(targetPlayer.name)
            appendSpace()
            success("wurde erfolgreich eingefroren.")
        }
    }
}