package de.cloudypanda.huntcraft.domain.notification.service

import de.cloudypanda.huntcraft.domain.notification.model.Notification
import de.cloudypanda.huntcraft.domain.notification.port.NotificationSender

/**
 * Domain service for notification functionality.
 * Contains core business logic for the notification domain.
 */
class NotificationService(
    private val notificationSender: NotificationSender
) {
    /**
     * Sends a notification to external systems.
     * @param notification the notification to send
     * @return true if the notification was sent successfully, false otherwise
     */
    fun sendNotification(notification: Notification): Boolean {
        return notificationSender.send(notification)
    }
    
    /**
     * Creates and sends a death notification.
     * @param playerName the name of the player who died
     * @param deathMessage the death message
     * @return true if the notification was sent successfully, false otherwise
     */
    fun sendDeathNotification(playerName: String, deathMessage: String): Boolean {
        val notification = Notification(
            type = de.cloudypanda.huntcraft.domain.notification.model.NotificationType.DEATH,
            message = deathMessage,
            playerName = playerName
        )
        return sendNotification(notification)
    }
    
    /**
     * Creates and sends an achievement notification.
     * @param playerName the name of the player who achieved something
     * @param achievementMessage the achievement message
     * @return true if the notification was sent successfully, false otherwise
     */
    fun sendAchievementNotification(playerName: String, achievementMessage: String): Boolean {
        val notification = Notification(
            type = de.cloudypanda.huntcraft.domain.notification.model.NotificationType.ACHIEVEMENT,
            message = achievementMessage,
            playerName = playerName
        )
        return sendNotification(notification)
    }
}