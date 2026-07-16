package dev.slne.surf.moderation.tools

import dev.slne.surf.moderation.tools.commands.*

object PaperCommandManager {
    fun registerCommands() {
        openPlayerInPanelCommand()
        surfModerationToolsCommand()
        faqCommand()
        rotateCommand()
        freezeCommand()
        unfreezeCommand()
        stopInteractionCommand()
        pingPlayerCommand()
        freezeListCommand()
    }
}