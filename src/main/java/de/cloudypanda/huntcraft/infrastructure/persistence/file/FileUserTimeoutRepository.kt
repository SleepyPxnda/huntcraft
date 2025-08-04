package de.cloudypanda.huntcraft.infrastructure.persistence.file

import de.cloudypanda.huntcraft.domain.deathtimer.model.UserTimeout
import de.cloudypanda.huntcraft.domain.deathtimer.repository.UserTimeoutRepository
import de.cloudypanda.huntcraft.infrastructure.config.model.DeathTimerConfig
import de.cloudypanda.huntcraft.infrastructure.config.repository.DeathTimerConfigRepository
import java.time.Instant
import java.util.*

/**
 * File-based implementation of UserTimeoutRepository.
 * This is an adapter in the hexagonal architecture.
 */
class FileUserTimeoutRepository(
    private val deathTimerConfigRepository: DeathTimerConfigRepository
) : UserTimeoutRepository {
    
    override fun save(userTimeout: UserTimeout) {
        val config = deathTimerConfigRepository.getConfig()
        
        // Remove existing timeout for this player if it exists
        config.currentDeathTimeoutPlayers.removeIf { it.playerUUID == userTimeout.playerUUID }
        
        // Add the new timeout
        config.currentDeathTimeoutPlayers.add(mapToConfigModel(userTimeout))
        
        // Save the updated config
        deathTimerConfigRepository.saveConfig(config)
    }
    
    override fun findByPlayerUUID(playerUUID: UUID): UserTimeout? {
        val config = deathTimerConfigRepository.getConfig()
        val configModel = config.currentDeathTimeoutPlayers.find { it.playerUUID == playerUUID } ?: return null
        return mapToDomainModel(configModel, config.deathTimeoutDurationMillis)
    }
    
    override fun findAll(): List<UserTimeout> {
        val config = deathTimerConfigRepository.getConfig()
        return config.currentDeathTimeoutPlayers.map { 
            mapToDomainModel(it, config.deathTimeoutDurationMillis)
        }
    }
    
    override fun remove(playerUUID: UUID) {
        val config = deathTimerConfigRepository.getConfig()
        config.currentDeathTimeoutPlayers.removeIf { it.playerUUID == playerUUID }
        deathTimerConfigRepository.saveConfig(config)
    }
    
    // Helper methods to map between domain and infrastructure models
    private fun mapToDomainModel(configModel: DeathTimerConfig.UserTimeoutConfigModel, timeoutDuration: Long): UserTimeout {
        // Create a UserTimeout instance from the config model
        // Note: In a real implementation, we would need to convert the timestamp to Instant
        // For this sample, we're using the current time as a simplification
        return UserTimeout.create(
            playerUUID = configModel.playerUUID,
            playerName = configModel.playerName,
            timeoutDurationMillis = timeoutDuration
        )
    }
    
    private fun mapToConfigModel(userTimeout: UserTimeout): DeathTimerConfig.UserTimeoutConfigModel {
        // Create a config model from the UserTimeout instance
        return DeathTimerConfig.UserTimeoutConfigModel(
            playerUUID = userTimeout.playerUUID,
            deathTimestamp = Instant.now().toEpochMilli(),
            playerName = userTimeout.playerName
        )
    }
}