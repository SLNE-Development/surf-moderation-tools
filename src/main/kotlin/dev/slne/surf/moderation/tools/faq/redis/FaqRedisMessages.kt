package dev.slne.surf.moderation.tools.faq.redis

import dev.slne.surf.redis.event.RedisEvent
import dev.slne.surf.redis.request.RedisRequest
import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftFaqsRequest(
    val refresh: Boolean = false
) : RedisRequest()

@Serializable
data class MinecraftFaqsResponse(
    val faqs: List<MinecraftFaqData>
) : RedisResponse()

@Serializable
data class MinecraftFaqData(
    val key: String,
    val question: String,
    val shortText: String
)

@Serializable
class FaqsChangedEvent : RedisEvent()

@Serializable
data class MinecraftFaqUsedEvent(
    val key: String,
    val senderUuid: String,
    val senderName: String
) : RedisEvent()
