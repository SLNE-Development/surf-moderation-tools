package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.api.paper.dialog.*
import dev.slne.surf.api.paper.dialog.builder.*
import dev.slne.surf.moderation.tools.faq.Faq
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import io.papermc.paper.registry.data.dialog.ActionButton
import org.bukkit.entity.Player

fun createEditFaqDialog(originalFaq: Faq, draftFaq: Faq = originalFaq) = dialog {
    base {
        title {
            primary("FAQ ")
            variableValue(draftFaq.id)
            primary(" bearbeiten")
        }
        input {
            text("faqName") {
                label { text("FAQ Name") }
                initial(draftFaq.id)
                maxLength(64)
                width(600)
            }
        }

        input {
            text("faqContent") {
                label { text("FAQ Inhalt") }
                initial(draftFaq.content)
                width(600)
                maxLength(Int.MAX_VALUE)
                multiline(Int.MAX_VALUE, 200)
            }
        }
    }

    type {
        multiAction {
            columns(2)
            action(createPreviewEditButton(originalFaq))
            action(createResetEditButton(originalFaq))
            action(createCancelEditButton(originalFaq))
            action(createSaveChangesButton(originalFaq))
        }
    }
}

private fun createResetEditButton(faq: Faq): ActionButton = actionButton {
    label { text("Zurücksetzen") }
    tooltip { info("Setzt alle Änderungen zurück") }
    action {
        playerCallback { player ->
            player.showDialog(createEditFaqDialog(faq))
        }
    }
}

private fun createCancelEditButton(faq: Faq): ActionButton = actionButton {
    label { text("Abbrechen") }
    tooltip { info("Abbrechen und zurück zu den FAQ Details") }
    action {
        playerCallback { player ->
            player.showDialog(createFaqDetailsDialog(faq))
        }
    }
}

private fun createMissingFaqNameNotice(player: Player, originalFaq: Faq, draft: Faq) = dialog {
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
                    player.showDialog(createEditFaqDialog(originalFaq, draft))
                }
            }
        }
    }
}


private fun createMissingFaqContentNotice(player: Player, originalFaq: Faq, draft: Faq) = dialog {
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
                    player.showDialog(createEditFaqDialog(originalFaq, draft))
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
private fun createPreviewEditButton(originalFaq: Faq): ActionButton = actionButton {
    label { text("Vorschau") }
    tooltip { info("Zeigt eine Vorschau des FAQ-Inhalts an") }
    action {
        customPlayerClick { input, player ->
            val rawFaqName = input.getText("faqName") ?: ""
            val faqContent = input.getText("faqContent")?.trim() ?: ""
            player.showDialog(createEditFaqPreviewDialog(originalFaq, Faq(rawFaqName, faqContent, originalFaq.enabled)))
        }
    }
}

@Suppress("UnstableApiUsage")
private fun createEditFaqPreviewDialog(originalFaq: Faq, draftFaq: Faq) = dialog {
    base {
        title {
            primary("FAQ Vorschau von ")
            variableValue(draftFaq.id)
        }
        body {
            plainMessage {
                append(draftFaq.asComponent())
            }
        }
    }
    type {
        notice {
            label { success("Ok") }
            action {
                playerCallback { player ->
                    player.showDialog(createEditFaqDialog(originalFaq, draftFaq))
                }
            }
        }
    }
}

private fun createSaveChangesButton(originalFaq: Faq): ActionButton = actionButton {
    label { text("Speichern") }
    tooltip { info("Speichert Änderungen") }
    action {
        customPlayerClick { input, player ->
            val rawFaqName = input.getText("faqName")
            val faqName = rawFaqName?.replace(" ", "-")?.trim() ?: ""
            val faqContent = input.getText("faqContent")?.trim() ?: ""
            if (faqName.isEmpty()) {
                player.showDialog(createMissingFaqNameNotice(player, originalFaq, Faq(rawFaqName ?: "", faqContent, originalFaq.enabled)))
                return@customPlayerClick
            }
            if (faqContent.isEmpty()) {
                player.showDialog(createMissingFaqContentNotice(player, originalFaq, Faq(rawFaqName ?: "", faqContent, originalFaq.enabled)))
                return@customPlayerClick
            }
            val existingFaqById = SurfModerationToolConfig.getConfig().faqs.find { it.id == faqName && it.id != originalFaq.id }

            if (existingFaqById != null) {
                player.showDialog(createExistingFaqByIdNotice(player, originalFaq, Faq(rawFaqName ?: "", faqContent, originalFaq.enabled)))
                return@customPlayerClick
            }


            Faq.persist(Faq(faqName, faqContent, originalFaq.enabled), true)

            player.showDialog(createEditSuccessNotice(player))

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
                    player.showDialog((createListFaqsDialog()))
                }
            }
        }
    }
}

private fun createExistingFaqByIdNotice(player: Player, originalFaq: Faq, draft: Faq) = dialog {
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
                    player.showDialog(createEditFaqDialog(originalFaq, draft))
                }
            }
        }
    }
}

