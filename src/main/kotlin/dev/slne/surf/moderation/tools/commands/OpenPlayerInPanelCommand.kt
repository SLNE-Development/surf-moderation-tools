package dev.slne.surf.moderation.tools.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

fun openPlayerInPanelCommand() = commandTree("openplayerinpanel") {
    withPermission(PermissionRegistry.COMMAND_OPEN_PLAYER_IN_PANEL)

    stringArgument("user") {
        anyExecutorSuspend { sender, args ->
            val user: String by args

            val uuid: UUID? = try {
                UUID.fromString(user)
            } catch (_: IllegalArgumentException) {
                PlayerLookupService.getUuid(user)
            }

            if (uuid == null) {
                sender.sendText {
                    appendErrorPrefix()
                    error("Der Spieler ")
                    variableValue(user)
                    error(" wurde nicht gefunden.")
                }
                return@anyExecutorSuspend
            }

            val url = "https://support.castcrafter.de/players/$uuid"

            sender.sendText {
                appendSuccessPrefix()
                primary("Panel-Link".toSmallCaps(), TextDecoration.BOLD)
                spacer(" • ")
                append {
                    variableValue(user)
                    hoverEvent(buildText {
                        info("Klicke, um die UUID zu kopieren.")
                        appendNewline()
                        spacer(uuid.toString())
                    })
                    clickCopiesToClipboard(uuid.toString())
                }
                appendNewSuccessPrefixedLine {
                    append(CommonComponents.EM_DASH)
                    appendSpace()
                    append {
                        spacer("[")
                        success("Panel öffnen", TextDecoration.BOLD)
                        spacer("]")
                        hoverEvent(buildText {
                            info("Klicke, um den Spieler ")
                            variableValue(user)
                            info(" im Panel zu öffnen.")
                            appendNewline()
                            spacer(url)
                        })
                        clickOpensUrl(url)
                    }
                    appendSpace()
                    append {
                        spacer("[")
                        info("Link kopieren", TextDecoration.BOLD)
                        spacer("]")
                        hoverEvent(buildText {
                            info("Klicke, um den Panel-Link zu kopieren.")
                            appendNewline()
                            spacer(url)
                        })
                        clickCopiesToClipboard(url)
                    }
                }
            }
        }
    }
}