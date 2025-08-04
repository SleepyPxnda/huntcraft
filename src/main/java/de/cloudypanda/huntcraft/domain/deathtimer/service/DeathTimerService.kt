package de.cloudypanda.huntcraft.domain.deathtimer.service

import de.cloudypanda.huntcraft.domain.deathtimer.model.UserTimeout
import de.cloudypanda.huntcraft.domain.deathtimer.repository.UserTimeoutRepository
import java.util.UUID

/**
 * Domain service for death timer functionality.
 * Contains core business logic for the death timer domain.
 */
class DeathTimerService(
    private val userTimeoutRepository: UserTimeoutRepository
) {
    /**
     * Records a player death and creates a timeout.
     */
    fun recordPlayerDeath(playerUUID: UUID, playerName: String, timeoutDurationMillis: Long) {
        val userTimeout = UserTimeout.create(
            playerUUID = playerUUID,
            playerName = playerName,
            timeoutDurationMillis = timeoutDurationMillis
        )
        userTimeoutRepository.save(userTimeout)
    }
    
    /**
     * Checks if a player is allowed to join the server.
     * @return true if the player can join, false otherwise
     */
    fun canPlayerJoin(playerUUID: UUID): Boolean {
        val userTimeout = userTimeoutRepository.findByPlayerUUID(playerUUID) ?: return true
        return userTimeout.isAllowedToJoin()
    }
    
    /**
     * Gets the remaining timeout for a player.
     * @return remaining timeout in milliseconds, 0 if no timeout or expired
     */
    fun getRemainingTimeout(playerUUID: UUID): Long {
        val userTimeout = userTimeoutRepository.findByPlayerUUID(playerUUID) ?: return 0
        return userTimeout.getRemainingTimeoutMillis()
    }
    
    /**
     * Gets the formatted rejoin time for a player.
     * @return formatted date string or null if no timeout
     */
    fun getFormattedRejoinTime(playerUUID: UUID): String? {
        val userTimeout = userTimeoutRepository.findByPlayerUUID(playerUUID) ?: return null
        return userTimeout.getFormattedRejoinTime()
    }
}