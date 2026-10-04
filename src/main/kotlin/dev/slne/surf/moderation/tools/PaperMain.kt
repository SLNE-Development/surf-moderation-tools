package dev.slne.surf.moderation.tools

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.moderation.tools.config.SurfModerationToolConfig
import dev.slne.surf.moderation.tools.listener.PlayerActionListener
import dev.slne.surf.moderation.tools.redis.RedisService
import dev.slne.surf.moderation.tools.service.FaqService
import org.bukkit.plugin.java.JavaPlugin


val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        SurfModerationToolConfig.init()
    }

    override suspend fun onEnableAsync() {
        RedisService.connect()
        FaqService.reload()

        PaperCommandManager.registerCommands()
        PlayerActionListener.register()
    }

    override suspend fun onDisableAsync() {
        RedisService.disconnect()
    }
}
