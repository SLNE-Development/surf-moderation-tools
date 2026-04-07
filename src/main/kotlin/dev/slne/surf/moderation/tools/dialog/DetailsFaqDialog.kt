package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.moderation.tools.faq.Faq
import io.papermc.paper.registry.data.dialog.ActionButton
import dev.slne.surf.api.paper.dialog.*
import dev.slne.surf.api.paper.dialog.builder.*

@Suppress("UnstableApiUsage")
fun createFaqDetailsDialog(faq: Faq) = dialog {
    base {
        title {
            primary("FAQ Details von ")
            variableValue(faq.id)
        }
        body {
            plainMessage {
                primary("Inhalt:")
            }
            plainMessage {
                append(faq.asComponent())
            }
        }
        type {
            multiAction {
                columns(2)
                action(toggleEnabledFaqButton(faq))
                action(editFaqButton(faq))
                action(deleteFaqButton(faq))
                exitAction(closeFaqDetailsDialogButton())
            }
        }
    }
}

@Suppress("UnstableApiUsage")
private fun toggleEnabledFaqButton(faq: Faq): ActionButton = actionButton {
    label {
        if (faq.enabled) text("Deaktivieren") else text("Aktivieren")
    }
    tooltip {
        if (faq.enabled) info("Deaktiviert dieses FAQ. Es wird nicht mehr angezeigt.")
        else info("Aktiviert dieses FAQ. Es wird wieder angezeigt.")
    }
    action {
        playerCallback { player ->
            val updated = faq.copy(enabled = !faq.enabled)
            Faq.persist(updated, true)
            player.showDialog(createToggleSuccessNotice(updated))
        }
    }
}

@Suppress("UnstableApiUsage")
private fun closeFaqDetailsDialogButton(): ActionButton = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zur FAQ Liste") }
    action {
        playerCallback { player ->
            player.showDialog(createListFaqsDialog())
        }
    }
}
@Suppress("UnstableApiUsage")
private fun editFaqButton(faq: Faq): ActionButton = actionButton {
    label { text("Bearbeiten") }
    tooltip { info("Bearbeitet dieses FAQ") }
    action {
        playerCallback { player ->
            player.showDialog(createEditFaqDialog(faq))
        }
    }
}

@Suppress("UnstableApiUsage")
private fun createToggleSuccessNotice(faq: Faq) = dialog {
    base {
        title { primary("FAQ Status") }
        body {
            plainMessage {
                success("Der FAQ-Status von")
                variableValue(" ${faq.id}")
                success(" wurde erfolgreich auf ")
                variableValue(if (faq.enabled) "aktiviert" else "deaktiviert")
                success(" geändert.")
            }
        }
    }
    type {
        notice {
            label { success("Ok") }
            action {
                playerCallback { player ->
                    player.showDialog(createFaqDetailsDialog(faq))
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
private fun deleteFaqButton(faq: Faq): ActionButton = actionButton {
    label { text("Löschen") }
    tooltip { info("Löscht dieses FAQ") }
    action {
        playerCallback { player ->
            player.showDialog(confirmDeleteFaqDialog(faq))
        }
    }
}

@Suppress("UnstableApiUsage")
private fun confirmDeleteFaqDialog(faq: Faq) = dialog {
    base {
        title {
            primary("FAQ löschen")
        }
        body {
            plainMessage {
                text("Willst du das FAQ ")
                variableValue(faq.id)
                text(" wirklich löschen?")
            }
        }
    }
    type {
        confirmation {
            yes {
                label { success("Ja") }
                action {
                    playerCallback { player ->
                        Faq.update(faq)
                        player.showDialog(createListFaqsDialog())
                    }
                }
            }
            no {
                label { error("Nein") }
                action {
                    playerCallback { player ->
                        player.showDialog(createFaqDetailsDialog(faq))
                    }
                }
            }
        }
    }
}
