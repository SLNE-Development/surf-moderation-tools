package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.api.paper.dialog.*
import dev.slne.surf.api.paper.dialog.builder.*
import dev.slne.surf.moderation.tools.faq.Faq
import io.papermc.paper.registry.data.dialog.ActionButton

fun createFaqSettingsDialog() = dialog {
    base {
        title {
            primary("FAQ Einstellungen")
        }
        type {
            multiAction {
                columns(2)
                action(createFaqButton())
                action(listFaqsButton())
                exitAction(closeDialogButton())
            }
        }
    }
}

private fun listFaqsButton(): ActionButton = actionButton {
    label { info("FAQ Liste") }
    tooltip { info("Zeigt alle FAQs an") }
    action {
        playerCallback { player ->
            player.showDialog(createListFaqsDialog())
        }
    }
}

private fun createFaqButton(): ActionButton = actionButton {
    label { info("FAQ erstellen") }
    tooltip { info("Erstellt ein neues FAQ") }
    action {
        playerCallback { player ->
            player.showDialog(createFaqDialog(Faq("", "")))
        }
    }
}

private fun closeDialogButton(): ActionButton = actionButton {
    label { text("Schließen") }
    tooltip { info("Dialog schließen") }
    action {
        playerCallback { player ->
            player.closeDialog()
        }
    }
}
