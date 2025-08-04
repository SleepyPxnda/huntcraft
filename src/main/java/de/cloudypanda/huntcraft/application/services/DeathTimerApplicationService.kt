package de.cloudypanda.huntcraft.application.services

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