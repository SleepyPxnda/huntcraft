package de.cloudypanda.huntcraft.domain.notification.port

import de.cloudypanda.huntcraft.domain.notification.model.Notification

/**
 * Port (interface) for sending notifications to external systems.
 * This is a port in the hexagonal architecture.
 */
interface NotificationSender {
    /**
     * Sends a notification to an external system.
     * @param notification the notification to send
     * @return true if the notification was sent successfully, false otherwise
     */
    fun send(notification: Notification): Boolean
}