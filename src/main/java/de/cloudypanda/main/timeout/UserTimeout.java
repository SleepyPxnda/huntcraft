package de.cloudypanda.main.timeout;

import java.util.UUID;

public class UserTimeout {

    public UUID playerUUID;
    public long latestDeath;

    public UserTimeout(UUID playerUUID, long latestDeath) {
        this.playerUUID = playerUUID;
        this.latestDeath = latestDeath;
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
}
