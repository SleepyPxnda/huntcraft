package de.cloudypanda.main.deathtimer.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTimeoutConfig {

    private UUID playerUUID;
    private long latestDeath;
    private String playerName;

    public boolean isAllowedToJoin(long deathTimeout) {
        return Instant.now().isAfter(Instant.ofEpochMilli(latestDeath + deathTimeout));
    }
}
