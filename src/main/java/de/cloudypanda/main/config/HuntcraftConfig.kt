package de.cloudypanda.main.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import java.util.*

@ConfigSerializable
data class HuntcraftConfig(
    val discord: DiscordConfig = DiscordConfig(),
    val infos: InfoConfig = InfoConfig(),
    val death: DeathConfig = DeathConfig()
) {
    @ConfigSerializable
    data class DiscordConfig(
        val enabled: Boolean = false,
        val webhookUrl: String = ""
    )

    @ConfigSerializable
    data class InfoConfig(
        val discordLink: String = "",
        val websiteLink: String = "",
        val rulesLink: String = ""
    )

    @ConfigSerializable
    data class DeathConfig(
        val deathTimer: Int = 60, // in seconds
        val playerTimeouts: MutableList<UserTimeoutConfig> = mutableListOf()
    )

    @ConfigSerializable
    data class UserTimeoutConfig(
        val playerUUID: UUID,
        val latestDeath: Long,
        val playerName: String
    ) {
        fun isAllowedToJoin(deathTimeout: Long): Boolean {
            return System.currentTimeMillis() > (latestDeath + deathTimeout * 1000)
        }
    }
}