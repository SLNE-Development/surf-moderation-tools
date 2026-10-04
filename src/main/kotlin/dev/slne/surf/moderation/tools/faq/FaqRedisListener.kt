package dev.slne.surf.moderation.tools.faq

import dev.slne.surf.moderation.tools.faq.redis.FaqsChangedEvent
import dev.slne.surf.moderation.tools.service.FaqService
import dev.slne.surf.redis.event.OnRedisEvent

object FaqRedisListener {
    @OnRedisEvent
    suspend fun onFaqsChanged(event: FaqsChangedEvent) {
        FaqService.reload()
    }
}
