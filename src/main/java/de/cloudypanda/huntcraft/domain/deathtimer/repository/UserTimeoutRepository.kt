package de.cloudypanda.huntcraft.domain.deathtimer.repository

import de.cloudypanda.huntcraft.domain.deathtimer.model.UserTimeout
import java.util.*

/**
 * Repository interface for UserTimeout entities.
 * This is a port in the hexagonal architecture.
 */
interface UserTimeoutRepository {
    /**
     * Saves a user timeout to the repository.
     */
    fun save(userTimeout: UserTimeout)
    
    /**
     * Finds a user timeout by player UUID.
     * @return the user timeout or null if not found
     */
    fun findByPlayerUUID(playerUUID: UUID): UserTimeout?
    
    /**
     * Gets all user timeouts.
     */
    fun findAll(): List<UserTimeout>
    
    /**
     * Removes a user timeout from the repository.
     */
    fun remove(playerUUID: UUID)
}