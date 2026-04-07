package dev.slne.surf.moderation.tools

import dev.slne.surf.moderation.tools.commands.*

object PaperCommandManager {
    fun registerCommands() {
        surfModerationToolsCommand()
        faqCommand()
        rotateCommand()
        freezeCommand()
        unfreezeCommand()
        stopInteraction()
        pingPlayerCommand()
    }
}