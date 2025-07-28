package de.cloudypanda.main.core.integrations.discord

interface NotificationManager {
    fun sendDeathMessage(deathMessage: String)
    fun sendAchievementMessage(message: String)
}