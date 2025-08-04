package de.cloudypanda.huntcraft.domain.notification.model

/**
 * Represents a notification to be sent to external systems.
 * This is a value object with no identity.
 */
data class Notification(
    val type: NotificationType,
    val message: String,
    val playerName: String
) {
    /**
     * Formats the notification message based on its type.
     * @return formatted message ready to be sent
     */
    fun getFormattedMessage(): String {
        return when (type) {
            NotificationType.DEATH -> "@here $playerName $message"
            NotificationType.ACHIEVEMENT -> "@here $playerName achieved: $message"
        }
    }
    
    /**
     * Gets the appropriate title for the notification based on its type.
     * @return title for the notification
     */
    fun getTitle(): String {
        return when (type) {
            NotificationType.DEATH -> "Death Bot"
            NotificationType.ACHIEVEMENT -> "Achievement Bot"
        }
    }
    
    /**
     * Gets the appropriate icon URL for the notification based on its type.
     * @return icon URL for the notification
     */
    fun getIconUrl(): String {
        return when (type) {
            NotificationType.DEATH -> "https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png"
            NotificationType.ACHIEVEMENT -> "https://www.iconpacks.net/icons/1/free-trophy-icon-1021-thumb.png"
        }
    }
}