package dev.slne.surf.moderation.tools.commands

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.moderation.tools.plugin
import dev.slne.surf.moderation.tools.service.FreezeService
import dev.slne.surf.moderation.tools.util.PermissionRegistry
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.time.toKotlinDuration

private val FREEZE_TIME_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(ZoneId.systemDefault())

fun freezeListCommand() = commandTree("freezelist") {
    withPermission(PermissionRegistry.COMMAND_FREEZE)

    integerArgument("page", optional = true) {
        anyExecutor { executor, args ->
            val page: Int? by args

            plugin.launch {
                val frozenPlayers = FreezeService.getFrozenEntries()
                    .entries
                    .sortedBy { it.value.expiresAt }

                if (frozenPlayers.isEmpty()) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Es sind aktuell keine Spieler eingefroren.")
                    }
                    return@launch
                }

                val pagination =
                    Pagination<Map.Entry<UUID, FreezeService.FreezeData>> {
                        title {
                            primary("Eingefrorene Spieler".toSmallCaps(), TextDecoration.BOLD)
                            spacer(" (${frozenPlayers.size})")
                        }
                        rowRenderer { entry, _ ->
                            val uuid = entry.key
                            val data = entry.value
                            val targetName = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
                            val remaining = Duration.between(Instant.now(), data.expiresAt)
                                .coerceAtLeast(Duration.ZERO)

                            listOf(
                                buildText {
                                    append(CommonComponents.EM_DASH)
                                    appendSpace()
                                    append {
                                        variableValue(targetName)
                                        hoverEvent(buildText {
                                            info("Eingefroren von: ")
                                            variableValue(data.frozenByName)
                                            appendNewline()
                                            info("Eingefroren am: ")
                                            variableValue(FREEZE_TIME_FORMATTER.format(data.frozenAt))
                                            appendNewline()
                                            info("Läuft ab am: ")
                                            variableValue(FREEZE_TIME_FORMATTER.format(data.expiresAt))
                                            appendNewline()
                                            info("Verbleibend: ")
                                            appendTime(remaining.toKotlinDuration())
                                        })
                                    }
                                    appendSpace()
                                    append {
                                        spacer("[")
                                        error("Entfrieren")
                                        spacer("]")
                                        hoverEvent(buildText {
                                            info("Klicke, um ")
                                            variableValue(targetName)
                                            info(" aufzutauen.")
                                        })
                                        clickRunsCommand("/unfreeze $targetName")
                                    }
                                }
                            )
                        }
                    }

                executor.sendText {
                    append(pagination.renderComponent(frozenPlayers, page ?: 1))
                }
            }
        }
    }
}
