# Sample DDD Implementation for Death Timer Domain

This document provides a concrete implementation example of the Death Timer domain following Domain-Driven Design principles. It demonstrates how to structure the code according to the architecture plan.

## Domain Layer

### Domain Model

```kotlin
// domain/deathtimer/model/UserTimeout.kt
package de.cloudypanda.huntcraft.domain.deathtimer.model

import java.time.Instant
import java.util.UUID

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
```

### Repository Interface

```kotlin
// domain/deathtimer/repository/UserTimeoutRepository.kt
package de.cloudypanda.huntcraft.domain.deathtimer.repository

import de.cloudypanda.huntcraft.domain.deathtimer.model.UserTimeout
import java.util.UUID

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
```

### Domain Service

```kotlin
// domain/deathtimer/service/DeathTimerService.kt
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
```

## Infrastructure Layer

### Repository Implementation

```kotlin
// infrastructure/persistence/file/FileUserTimeoutRepository.kt
package de.cloudypanda.huntcraft.infrastructure.persistence.file

import de.cloudypanda.huntcraft.domain.deathtimer.model.UserTimeout
import de.cloudypanda.huntcraft.domain.deathtimer.repository.UserTimeoutRepository
import de.cloudypanda.huntcraft.infrastructure.config.model.DeathTimerConfig
import de.cloudypanda.huntcraft.infrastructure.config.repository.DeathTimerConfigRepository
import java.util.UUID

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
        return UserTimeout.create(
            playerUUID = configModel.playerUUID,
            playerName = configModel.playerName,
            timeoutDurationMillis = timeoutDuration
        )
    }
    
    private fun mapToConfigModel(userTimeout: UserTimeout): DeathTimerConfig.UserTimeoutConfigModel {
        return DeathTimerConfig.UserTimeoutConfigModel(
            playerUUID = userTimeout.playerUUID,
            deathTimestamp = userTimeout.deathTimestamp.toEpochMilli(),
            playerName = userTimeout.playerName
        )
    }
}
```

## Application Layer

### Application Service

```kotlin
// application/service/DeathTimerApplicationService.kt
package de.cloudypanda.huntcraft.application.service

import de.cloudypanda.huntcraft.domain.deathtimer.service.DeathTimerService
import de.cloudypanda.huntcraft.domain.notification.model.Notification
import de.cloudypanda.huntcraft.domain.notification.model.NotificationType
import de.cloudypanda.huntcraft.domain.notification.service.NotificationService
import org.bukkit.entity.Player
import java.util.UUID

/**
 * Application service for death timer functionality.
 * Orchestrates the use cases involving the death timer domain.
 */
class DeathTimerApplicationService(
    private val deathTimerService: DeathTimerService,
    private val notificationService: NotificationService,
    private val timeoutDurationMillis: Long
) {
    /**
     * Handles a player death event.
     * Records the death, creates a timeout, and sends notifications.
     */
    fun handlePlayerDeath(player: Player, deathMessage: String) {
        // Record the death in the domain
        deathTimerService.recordPlayerDeath(
            playerUUID = player.uniqueId,
            playerName = player.name,
            timeoutDurationMillis = timeoutDurationMillis
        )
        
        // Create and send a notification
        val notification = Notification(
            type = NotificationType.DEATH,
            message = deathMessage,
            playerName = player.name
        )
        notificationService.sendNotification(notification)
    }
    
    /**
     * Checks if a player is allowed to join and returns appropriate messages.
     * @return a pair of (allowed, message)
     */
    fun checkPlayerJoin(playerUUID: UUID, playerName: String): Pair<Boolean, String?> {
        val canJoin = deathTimerService.canPlayerJoin(playerUUID)
        
        if (!canJoin) {
            val rejoinTime = deathTimerService.getFormattedRejoinTime(playerUUID)
            val message = "You are currently on death timeout. You can rejoin at: $rejoinTime"
            return Pair(false, message)
        }
        
        return Pair(true, null)
    }
}
```

### Event Listener

```kotlin
// application/event/DeathTimerEventListener.kt
package de.cloudypanda.huntcraft.application.event

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.huntcraft.application.service.DeathTimerApplicationService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

/**
 * Event listener for death timer related events.
 * Delegates to application services.
 */
class DeathTimerEventListener(
    private val deathTimerApplicationService: DeathTimerApplicationService
) : Listener {

    @EventHandler
    fun onDeathEvent(event: PlayerDeathEvent) {
        val deathMessage = PlainTextComponentSerializer.plainText().serialize(event.deathMessage() ?: Component.text(""))
        deathTimerApplicationService.handlePlayerDeath(event.player, deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(event: PlayerPostRespawnEvent) {
        val (canJoin, message) = deathTimerApplicationService.checkPlayerJoin(
            event.player.uniqueId,
            event.player.name
        )
        
        if (!canJoin && message != null) {
            event.player.kick(Component.text(message))
        }
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(event: AsyncPlayerPreLoginEvent) {
        val (canJoin, message) = deathTimerApplicationService.checkPlayerJoin(
            event.uniqueId,
            event.name
        )
        
        if (!canJoin && message != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text(message))
        }
    }
}
```

## Main Plugin Class

```kotlin
// HuntcraftPlugin.kt
package de.cloudypanda.huntcraft

import de.cloudypanda.huntcraft.application.event.CoreEventListener
import de.cloudypanda.huntcraft.application.event.DeathTimerEventListener
import de.cloudypanda.huntcraft.application.service.DeathTimerApplicationService
import de.cloudypanda.huntcraft.domain.deathtimer.repository.UserTimeoutRepository
import de.cloudypanda.huntcraft.domain.deathtimer.service.DeathTimerService
import de.cloudypanda.huntcraft.domain.notification.service.NotificationService
import de.cloudypanda.huntcraft.infrastructure.config.repository.CoreConfigRepository
import de.cloudypanda.huntcraft.infrastructure.config.repository.DeathTimerConfigRepository
import de.cloudypanda.huntcraft.infrastructure.integration.discord.DiscordNotificationSender
import de.cloudypanda.huntcraft.infrastructure.persistence.file.FileUserTimeoutRepository
import org.bukkit.plugin.java.JavaPlugin

class HuntcraftPlugin : JavaPlugin() {

    // Repositories
    private lateinit var coreConfigRepository: CoreConfigRepository
    private lateinit var deathTimerConfigRepository: DeathTimerConfigRepository
    private lateinit var userTimeoutRepository: UserTimeoutRepository
    
    // Domain Services
    private lateinit var deathTimerService: DeathTimerService
    private lateinit var notificationService: NotificationService
    
    // Application Services
    private lateinit var deathTimerApplicationService: DeathTimerApplicationService
    
    override fun onEnable() {
        // Initialize repositories
        coreConfigRepository = CoreConfigRepository(this, "hc_core")
        deathTimerConfigRepository = DeathTimerConfigRepository(this, "hc_deathtimer")
        userTimeoutRepository = FileUserTimeoutRepository(deathTimerConfigRepository)
        
        // Initialize domain services
        deathTimerService = DeathTimerService(userTimeoutRepository)
        
        // Initialize notification infrastructure
        val coreConfig = coreConfigRepository.getConfig()
        val discordNotificationSender = DiscordNotificationSender(
            webhookUrl = coreConfig.webhook.webhookUrl,
            enabled = coreConfig.webhook.enabled
        )
        
        // Initialize notification service
        notificationService = NotificationService(discordNotificationSender)
        
        // Initialize application services
        deathTimerApplicationService = DeathTimerApplicationService(
            deathTimerService = deathTimerService,
            notificationService = notificationService,
            timeoutDurationMillis = coreConfig.deathTimer.timeoutDurationMillis
        )
        
        // Register event listeners
        server.pluginManager.registerEvents(CoreEventListener(), this)
        
        if (coreConfig.deathTimer.enabled) {
            server.pluginManager.registerEvents(
                DeathTimerEventListener(deathTimerApplicationService),
                this
            )
            logger.info("DeathTimer Module is enabled")
        }
    }
}
```

## Benefits of This Implementation

1. **Clear Separation of Concerns**:
   - Domain models contain business logic
   - Repositories handle data access
   - Application services orchestrate use cases
   - Event listeners handle external events

2. **Testability**:
   - All dependencies are injected
   - Domain logic can be tested in isolation
   - Repositories can be mocked for testing

3. **Flexibility**:
   - Easy to add new features or modify existing ones
   - Clear boundaries between components

4. **Maintainability**:
   - Code organization reflects business domains
   - Dependencies are explicit
   - Each component has a single responsibility

5. **Scalability**:
   - New domains can be added without affecting existing ones
   - Infrastructure can be changed without affecting domain logic