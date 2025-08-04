package de.cloudypanda.huntcraft.infrastructure.integration.discord

import de.cloudypanda.huntcraft.domain.notification.model.Notification
import de.cloudypanda.huntcraft.domain.notification.port.NotificationSender
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Discord implementation of NotificationSender.
 * This is an adapter in the hexagonal architecture.
 */
class DiscordNotificationSender(
    private val webhookUrl: String,
    private val enabled: Boolean
) : NotificationSender {
    
    private val client: OkHttpClient = OkHttpClient()
    
    override fun send(notification: Notification): Boolean {
        if (!enabled) return false
        
        try {
            val requestContent = """
                {
                    "username": "${notification.getTitle()}",
                    "avatar_url": "${notification.getIconUrl()}",
                    "content": "${notification.getFormattedMessage()}"
                }
            """.trimIndent()
            
            val request = Request.Builder()
                .url(webhookUrl)
                .post(requestContent.toRequestBody())
                .header("Content-Type", "application/json")
                .build()
            
            client.newCall(request).execute().use { response ->
                return response.isSuccessful
            }
        } catch (e: Exception) {
            // Log the error in a real implementation
            return false
        }
    }
}