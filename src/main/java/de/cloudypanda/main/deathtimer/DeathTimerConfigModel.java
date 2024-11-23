package de.cloudypanda.main.deathtimer;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DeathTimerConfigModel {
    public long deathTimeout = 86400000L;
    public List<UserTimeout> currentDeathTimeOutetPlayers = new ArrayList<>();
}
