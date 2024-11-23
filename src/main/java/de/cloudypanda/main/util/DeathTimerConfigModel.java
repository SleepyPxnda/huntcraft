package de.cloudypanda.main.util;

import de.cloudypanda.main.timeout.UserTimeout;

import java.util.ArrayList;
import java.util.List;

public class DeathTimerConfigModel {

    public long deathTimeout = 86400000L;

    public List<UserTimeout> currentDeathTimeOutetPlayers = new ArrayList<>();

    public long getDeathTimeout() {
        return deathTimeout;
    }

    public void setDeathTimeout(long deathTimeout) {
        this.deathTimeout = deathTimeout;
    }

    public List<UserTimeout> getCurrentDeathTimeOutetPlayers() {
        return currentDeathTimeOutetPlayers;
    }

    public void setCurrentDeathTimeOutetPlayers(List<UserTimeout> currentDeathTimeOutetPlayers) {
        this.currentDeathTimeOutetPlayers = currentDeathTimeOutetPlayers;
    }
}
