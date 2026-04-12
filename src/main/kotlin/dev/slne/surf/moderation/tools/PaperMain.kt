package dev.slne.surf.moderation.tools

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.listener.PlayerActionListener
import org.bukkit.plugin.java.JavaPlugin


val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        SurfModerationToolConfig.init()
    }

    override suspend fun onEnableAsync() {
        PaperCommandManager.registerCommands()
        PlayerActionListener.register()
    }
}
