package de.cloudypanda.huntcraft.infrastructure.config.repository

import de.cloudypanda.huntcraft.infrastructure.config.model.DeathTimerConfig

/**
 * Repository interface for DeathTimerConfig.
 * This is an infrastructure concern, not a domain repository.
 */
interface DeathTimerConfigRepository {
    /**
     * Gets the current death timer configuration.
     * @return the current configuration
     */
    fun getConfig(): DeathTimerConfig
    
    /**
     * Saves the death timer configuration.
     * @param config the configuration to save
     */
    fun saveConfig(config: DeathTimerConfig)
    
    /**
     * Creates the default configuration file if it doesn't exist.
     * @return the created or existing configuration
     */
    fun createFileIfNotExists(): DeathTimerConfig
}