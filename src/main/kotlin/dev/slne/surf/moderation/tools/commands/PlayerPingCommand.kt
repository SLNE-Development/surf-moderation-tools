package dev.slne.surf.moderation.tools.commands

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.jorel.commandapi.kotlindsl.booleanArgument
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.entitySelectorArgumentOnePlayer
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import dev.slne.surf.moderation.tools.util.appendArtyPrefix
import kotlinx.coroutines.withContext
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player

fun pingPlayerCommand() = commandAPICommand("pingPlayer") {
    entitySelectorArgumentOnePlayer("target")
    booleanArgument("message")
    withPermission(PermissionRegistry.COMMAND_PING_PLAYER)

    anyExecutorSuspend { sender, args ->

        val target: Player by args
        val message: Boolean by args

        withContext(plugin.entityDispatcher(target)) {
            if (message) {
                target.sendText {
                    appendArtyPrefix()
                    variableValue("@${target.name}", TextDecoration.BOLD)
                    appendSpace()
                    text("Bist du da?")
                }
            }

            target.playSound(useSelfEmitter = true) {
                type(Sound.BLOCK_BELL_USE)
                source(Source.MASTER)
                volume(1f)
                pitch(1f)
            }

            sender.sendText {
                appendSuccessPrefix()
                variableValue(target.name)
                success(" wurde erfolgreich gepingt.")
            }
        }
    }
}

