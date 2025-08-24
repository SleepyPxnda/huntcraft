package de.cloudypanda.main.config

import de.cloudypanda.main.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class HuntcraftConfig(
    val discord: DiscordConfig = DiscordConfig(),
    val infos: InfoConfig = InfoConfig(),
    val death: DeathConfig = DeathConfig()
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
        val deathTimer: Int = 60, // in seconds
        val playerTimeouts: MutableList<UserTimeoutConfig> = mutableListOf()
    )

    @Serializable
    data class UserTimeoutConfig(
        @Serializable(with = UUIDSerializer::class)
        val playerUUID: UUID,
        val latestDeath: Long,
        val playerName: String
    ) {
        fun isAllowedToJoin(deathTimeout: Long): Boolean {
            return System.currentTimeMillis() > (latestDeath + deathTimeout * 1000)
        }
    }
}