package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.surfapi.bukkit.api.dialog.*
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton


fun listFaqsDialog() = dialog {
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
                            text(faq.id)
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
