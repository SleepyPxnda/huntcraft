package de.cloudypanda.config

import kotlinx.serialization.Serializable

@Serializable
data class HuntcraftConfig(
    val discord: DiscordConfig = DiscordConfig(),
    val infos: InfoConfig = InfoConfig(),
    val death: DeathConfig = DeathConfig(),
) {
    @Serializable
    data class DiscordConfig(
        val enabled: Boolean = false,
        val webhookUrl: String = ""
    )

    @Serializable
    data class InfoConfig(
        val discordLink: String = "",
        val websiteLink: String = "",
        val rulesLink: String = ""
    )

    @Serializable
    data class DeathConfig(
        val deathTimer: Long = 60
    )
}