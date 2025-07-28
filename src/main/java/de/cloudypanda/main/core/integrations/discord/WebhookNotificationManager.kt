package de.cloudypanda.main.core.integrations.discord


class WebhookNotificationManager : NotificationManager {
    override fun sendDeathMessage(deathMessage: String) {
        val webhook = DiscordWebhookMessage(
            String.format("@here %s", deathMessage),
            "Death Bot",
            "https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png"
        )
        webhook.send()
    }

    override fun sendAchievementMessage(message: String) {
        val webhook = DiscordWebhookMessage(
            String.format("@here %s", message),
            "Achievement Bot",
            "https://www.iconpacks.net/icons/1/free-trash-icon-347-thumb.png"
        )
        webhook.send()
    }
}
