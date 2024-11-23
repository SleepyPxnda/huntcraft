package de.cloudypanda.main.deathtimer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class UserTimeout {

    private UUID playerUUID;
    private long latestDeath;
    private String playerName;

    public UserTimeout(UUID playerUUID, long latestDeath, String playerName) {
        this.playerUUID = playerUUID;
        this.latestDeath = latestDeath;
        this.playerName = playerName;
    }

    public boolean isAllowedToJoin(long deathTimeout) {
        return Instant.now().isAfter(Instant.ofEpochMilli(latestDeath + deathTimeout));
    }
}
