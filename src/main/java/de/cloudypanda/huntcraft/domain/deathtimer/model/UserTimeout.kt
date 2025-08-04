package de.cloudypanda.huntcraft.domain.deathtimer.model

import java.time.Instant
import java.util.*

/**
 * Represents a player's death timeout.
 * This is an entity with UUID as its identifier.
 */
class UserTimeout private constructor(
    val playerUUID: UUID,
    val playerName: String,
    val deathTimestamp: Instant,
    val timeoutDurationMillis: Long
) {
    companion object {
        fun create(playerUUID: UUID, playerName: String, timeoutDurationMillis: Long): UserTimeout {
            return UserTimeout(
                playerUUID = playerUUID,
                playerName = playerName,
                deathTimestamp = Instant.now(),
                timeoutDurationMillis = timeoutDurationMillis
            )
        }
    }

    /**
     * Checks if the player is allowed to join based on their timeout status.
     * @return true if the player can join, false otherwise
     */
    fun isAllowedToJoin(): Boolean {
        val currentTime = Instant.now()
        val timeoutEndTime = deathTimestamp.plusMillis(timeoutDurationMillis)
        return currentTime.isAfter(timeoutEndTime)
    }

    /**
     * Calculates the remaining timeout duration in milliseconds.
     * @return remaining timeout in milliseconds, 0 if timeout has expired
     */
    fun getRemainingTimeoutMillis(): Long {
        val currentTime = Instant.now()
        val timeoutEndTime = deathTimestamp.plusMillis(timeoutDurationMillis)
        
        if (currentTime.isAfter(timeoutEndTime)) {
            return 0
        }
        
        return timeoutEndTime.toEpochMilli() - currentTime.toEpochMilli()
    }

    /**
     * Gets the formatted date when the player can rejoin.
     * @return formatted date string
     */
    fun getFormattedRejoinTime(): String {
        val rejoinTime = deathTimestamp.plusMillis(timeoutDurationMillis)
        // Formatting logic would go here
        return rejoinTime.toString()
    }
}