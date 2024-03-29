package de.cloudypanda.main.timeout;

import java.time.Instant;
import java.util.UUID;

public class UserTimeout {

    public UUID playerUUID;
    public long latestDeath;
    public String playerName;

    public UserTimeout() {
    }

    public UserTimeout(UUID playerUUID, long latestDeath, String playerName) {
        this.playerUUID = playerUUID;
        this.latestDeath = latestDeath;
        this.playerName = playerName;
    }
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public long getLatestDeath() {
        return latestDeath;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public void setLatestDeath(long latestDeath) {
        this.latestDeath = latestDeath;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isAllowedToJoin(long deathTimeout) {
        return Instant.now().isAfter(Instant.ofEpochMilli(latestDeath + deathTimeout));
    }
}
