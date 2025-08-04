package de.cloudypanda.huntcraft.infrastructure.config.model

import java.util.*

/**
 * Configuration model for the Death Timer feature.
 * This is an infrastructure concern, not a domain model.
 */
data class DeathTimerConfig(
    val deathTimeoutDurationMillis: Long = 3600000, // 1 hour by default
    val currentDeathTimeoutPlayers: MutableList<UserTimeoutConfigModel> = mutableListOf()
) {
    /**
     * Configuration model for a user's death timeout.
     * This is an infrastructure concern, not a domain model.
     */
    data class UserTimeoutConfigModel(
        val playerUUID: UUID,
        val deathTimestamp: Long, // Epoch millis
        val playerName: String
    )
}