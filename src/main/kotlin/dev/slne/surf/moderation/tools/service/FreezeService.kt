package dev.slne.surf.moderation.tools.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import java.time.Duration
import java.time.Instant
import java.util.*

object FreezeService {

    data class FreezeData(
        val frozenBy: UUID?,
        val frozenByName: String,
        val frozenAt: Instant,
        val expiresAt: Instant,
    )

    private val frozenPlayers = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfter(Expiry.writing<UUID, FreezeData> { _, data ->
            Duration.between(data.frozenAt, data.expiresAt).coerceAtLeast(Duration.ZERO)
        })
        .build<UUID, FreezeData>()

    fun freeze(uuid: UUID, durationMs: Long, frozenBy: UUID?, frozenByName: String) {
        val now = Instant.now()
        frozenPlayers.put(
            uuid,
            FreezeData(frozenBy, frozenByName, now, now.plusMillis(durationMs))
        )
    }

    fun unfreeze(uuid: UUID) {
        frozenPlayers.invalidate(uuid)
    }

    fun isFrozen(uuid: UUID): Boolean {
        return frozenPlayers.getIfPresent(uuid) != null
    }

    fun getFrozenEntries(): Map<UUID, FreezeData> = frozenPlayers.asMap().toMap()
}
