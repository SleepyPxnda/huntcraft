package de.cloudypanda.main.deathtimer.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;


data class UserTimeoutConfig(val playerUUID: UUID, val latestDeath: Long, val playerName: String) {
    fun isAllowedToJoin(deathTimeout: Long): Boolean {
        return Instant.now().isAfter(Instant.ofEpochMilli(latestDeath + deathTimeout));
    }
}
