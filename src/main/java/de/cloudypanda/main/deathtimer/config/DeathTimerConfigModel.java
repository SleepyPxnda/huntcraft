package de.cloudypanda.main.deathtimer.config;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DeathTimerConfigModel {
    public long deathTimeout = 86400000L;
    public List<UserTimeoutConfig> currentDeathTimeOutetPlayers = new ArrayList<>();
}
