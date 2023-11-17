package de.cloudypanda.main.timeout;

import java.util.UUID;

public class UserTimeout {

    public UUID playerUUID;
    public long latestDeath;

    public String playerName;

    public UserTimeout(UUID playerUUID, long latestDeath, String playerName) {
        this.playerUUID = playerUUID;
        this.latestDeath = latestDeath;
        this.playerName = playerName;
    }

    public UserTimeout() {
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public long getLatestDeath() {
        return latestDeath;
    }

    public void setLatestDeath(long latestDeath) {
        this.latestDeath = latestDeath;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
