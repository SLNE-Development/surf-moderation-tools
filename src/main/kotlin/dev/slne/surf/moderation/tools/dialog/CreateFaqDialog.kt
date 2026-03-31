package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.surfapi.bukkit.api.dialog.*
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.*
import io.papermc.paper.registry.data.dialog.ActionButton
import org.bukkit.entity.Player

fun createFaqDialog() = dialog {
    base {
        title { primary("FAQ erstellen") }

        input {
            text("faqName") {
                label { text("FAQ Name") }
                maxLength(64)
                width(600)
            }
        }

        input {
            text("faqContent") {
                label { text("FAQ Inhalt") }
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 200)
            }
        }
    }

        type {
            multiAction {
                columns(2)
                action(createFaqButton())
                action(cancelCreateButton())
            }
        }
    }


private fun createFaqButton(): ActionButton = actionButton {
    label { text("Erstellen") }
    tooltip { info("FAQ erstellen") }
    action {
        customPlayerClick { input, player ->
            val rawFaqName = input.getText("faqName")
            val faqName = rawFaqName?.replace(" ", "-")?.trim() ?: ""
            val faqContent = input.getText("faqContent")?.trim() ?: ""
            if (faqName.isEmpty()) {
                player.showDialog(createMissingFaqNameNotice(player))
                return@customPlayerClick
            }
            if (faqContent.isEmpty()) {
                player.showDialog(createMissingFaqContentNotice(player))
                return@customPlayerClick
            }
            val faq = Faq(faqName, faqContent)
            val existingFaqById = SurfModerationToolConfig.getConfig().faqs.find { it.id == faqName }
            if (existingFaqById != null) {
                player.showDialog(createExistingFaqByIdNotice(player))
            } else {
                SurfModerationToolConfig.edit {
                    faqs.add(faq)
                }
                SurfModerationToolConfig.save()
                player.showDialog(createSuccessNotice(player))
            }
        }
    }
}

private fun createMissingFaqNameNotice(player: Player) = dialog {
    base {
        title { error("Kein FAQ-Name angegeben") }
        body {
            plainMessage {
                error("Bitte gib einen FAQ-Namen ein.")
            }
        }
    }
    type {
        notice {
            label { error("Ok") }
            action {
                playerCallback {
                    player.showDialog(createFaqDialog())
                }
            }
        }
    }
}

private fun createMissingFaqContentNotice(player: Player) = dialog {
    base {
        title { error("Kein FAQ-Inhalt angegeben") }
        body {
            plainMessage {
                error("Bitte gib einen FAQ-Inhalt ein.")
            }
        }
    }
    type {
        notice {
            label { error("Ok") }
            action {
                playerCallback {
                    player.showDialog(createFaqDialog())
                }
            }
        }
    }
}

private fun cancelCreateButton(): ActionButton = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zu den Einstellungen") }
    action {
        playerCallback { player ->
            player.showDialog(createFaqSettingsDialog())
        }
    }
}

private fun createSuccessNotice(player: Player) = dialog {
    base {
        title { info("FAQ erstellt") }
        body {
            plainMessage {
                success("Das FAQ wurde erfolgreich hinzugefügt.")
            }
        }
    }
    type {
        notice {
            label { success("Ok") }
            action {
                playerCallback {
                    player.showDialog(createFaqSettingsDialog())
                }
            }
        }
    }
}

private fun createExistingFaqByIdNotice(player: Player) = dialog {
    base {
        title { error("FAQ existiert bereits") }
        body {
            plainMessage {
                error("Ein FAQ mit diesem Namen existiert bereits. Bitte wähle einen anderen Namen.")
            }
        }
    }
    type {
        notice {
            label { error("Ok") }
            action {
                playerCallback {
                    player.showDialog(createFaqDialog())
                }
            }
        }
    }
}
