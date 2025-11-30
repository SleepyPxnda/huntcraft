package de.cloudypanda.core.integrations.discord


class WebhookNotificationManager : NotificationManager {
    override fun sendDeathMessage(deathMessage: String) {
        val webhook = DiscordWebhookMessage(
            String.format("@here %s", deathMessage),
            "Death Notifier",
            "https://api.deepai.org/job-view-file/07702799-61e1-4283-9246-fbdeb60bd8dc/outputs/output.jpg"
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
