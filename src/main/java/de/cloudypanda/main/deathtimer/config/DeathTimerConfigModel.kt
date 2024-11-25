package de.cloudypanda.main.deathtimer.config;

data class DeathTimerConfigModel(
    val deathTimeout: Long = 86400000L,
    val currentDeathTimeOutetPlayers: MutableList<UserTimeoutConfig> = ArrayList()
) {}
