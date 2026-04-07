package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.api.paper.dialog.*
import dev.slne.surf.api.paper.dialog.builder.*

fun createListFaqsDialog() = dialog {
    base {
        title { primary("FAQ Liste") }
    }
    type {
        multiAction {
            columns(1)
            SurfModerationToolConfig.getConfig().faqs
                .sortedBy { it.id }
                .forEach { faq ->
                    action {
                        label {
                            if (faq.enabled) {
                                success(faq.id)
                            } else {
                                error(faq.id)
                            }
                        }
                        tooltip {
                            if (faq.enabled) {
                                info("Aktiviert")
                            } else {
                                info("Deaktiviert")
                            }
                        }
                        columns(2)
                        exitAction(exitButton())
                        action {
                            playerCallback { player ->
                                player.showDialog(createFaqDetailsDialog(faq))
                            }
                        }
                    }
                }
        }
    }
}

private fun exitButton() = actionButton {
    label { text("Zurück") }
    tooltip { info("Zurück zu den FAQ Einstellungen") }

    action {
        playerCallback { player ->
            player.showDialog(createFaqSettingsDialog())
        }
    }
}
