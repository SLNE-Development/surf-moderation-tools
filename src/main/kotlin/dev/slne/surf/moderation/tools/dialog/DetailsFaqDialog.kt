package dev.slne.surf.moderation.tools.dialog

import dev.slne.surf.surfapi.bukkit.api.dialog.*
import dev.slne.surf.surfapi.bukkit.api.dialog.builder.actionButton
import io.papermc.paper.registry.data.dialog.ActionButton
import dev.slne.surf.moderation.tools.faq.Faq

@Suppress("UnstableApiUsage")
fun createFaqDetailsDialog(faq: Faq) = dialog {
    base {
        title {
            primary("FAQ Details von ")
            variableValue(faq.id)
        }
        type {
            multiAction {
                columns(2)
                action(toggleEnabledFaqButton(faq))
                action(deleteFaqButton(faq))
                action(editFaqButton(faq))
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
            dev.slne.surf.moderation.tools.config.SurfModerationToolConfig.edit {
                val entry = faqs.find { it.id == faq.id }
                if (entry != null) entry.enabled = !entry.enabled
            }
            dev.slne.surf.moderation.tools.config.SurfModerationToolConfig.save()
            val updated = dev.slne.surf.moderation.tools.config.SurfModerationToolConfig.getConfig().faqs.find { it.id == faq.id } ?: faq
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
            player.showDialog(listFaqsDialog())
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
                variableValue("${faq.id}")
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
                        dev.slne.surf.moderation.tools.config.SurfModerationToolConfig.edit {
                            faqs.removeIf { it.id == faq.id }
                        }
                        dev.slne.surf.moderation.tools.config.SurfModerationToolConfig.save()
                        player.showDialog(listFaqsDialog())
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
