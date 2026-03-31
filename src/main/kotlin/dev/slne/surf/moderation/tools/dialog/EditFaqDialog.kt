package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.surfapi.bukkit.api.dialog.*
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.*
import io.papermc.paper.registry.data.dialog.ActionButton
import org.bukkit.entity.Player

fun createEditFaqDialog(faq: Faq) = dialog {
    base {
        title {
            primary("FAQ ")
            variableValue(faq.id)
            primary(" bearbeiten")
        }
        input {
            text("faqName") {
                label { text("FAQ Name") }
                initial(faq.id)
                maxLength(64)
                width(600)
            }
        }

        input {
            text("faqContent") {
                label { text("FAQ Inhalt") }
                initial(faq.content)
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 200)
            }
        }

        type {
            multiAction {
                columns(2)
                action(cancelEditButton(faq))
                action(createSaveChangesButton(faq))
            }
        }
    }
}


private fun cancelEditButton(faq: Faq): ActionButton = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zu den FAQ Details") }
    action {
        playerCallback { player ->
            player.showDialog(createFaqDetailsDialog(faq))
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
                    player.showDialog(createEditFaqDialog(Faq("", "")))
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
                    player.showDialog(createEditFaqDialog(Faq("", "")))
                }
            }
        }
    }
}

private fun createSaveChangesButton(faq: Faq): ActionButton = actionButton {
    label { text("Speichern") }
    tooltip { info("Speichert Änderungen") }
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
            val existingFaqById = SurfModerationToolConfig.getConfig().faqs.find { it.id == faqName && it.id != faq.id }
            if (existingFaqById != null) {
                player.showDialog(createExistingFaqByIdNotice(player))
            } else {
                SurfModerationToolConfig.edit {
                    faqs.removeIf { it.id == faq.id }
                    faqs.add(Faq(faqName, faqContent))
                }
                SurfModerationToolConfig.save()
                player.showDialog(createEditSuccessNotice(player))
            }
        }
    }
}

private fun createEditSuccessNotice(player: Player) = dialog {
    base {
        title { info("FAQ gespeichert") }
        body {
            plainMessage {
                success("Das FAQ wurde erfolgreich gespeichert.")
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
                    player.showDialog(createEditFaqDialog(Faq("", "")))
                }
            }
        }
    }
}

