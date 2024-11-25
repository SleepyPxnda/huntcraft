package de.cloudypanda.main.deathtimer.config;

import java.time.Instant
import java.util.*


data class UserTimeoutConfig(val playerUUID: UUID, val latestDeath: Long, val playerName: String) {
    fun isAllowedToJoin(deathTimeout: Long): Boolean {
        return Instant.now().isAfter(Instant.ofEpochMilli(latestDeath + deathTimeout));
    }
}