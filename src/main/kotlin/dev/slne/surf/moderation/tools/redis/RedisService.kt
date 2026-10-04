package dev.slne.surf.moderation.tools.redis

import dev.slne.surf.moderation.tools.faq.FaqRedisListener
import dev.slne.surf.redis.RedisApi

object RedisService {
    val redisApi = RedisApi.create("surf-moderation-tools")

    fun connect() {
        redisApi.subscribeToEvents(FaqRedisListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        if (redisApi.isConnected()) {
            redisApi.disconnect()
        }
    }
}
