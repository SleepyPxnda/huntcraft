package de.cloudypanda.core.integrations.discord

interface NotificationManager {
    fun sendDeathMessage(deathMessage: String)
    fun sendAchievementMessage(message: String)
}