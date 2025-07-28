package de.cloudypanda.main.config.deathtimer

data class DeathTimerConfigModel(
    val deathTimeout: Long = 86400000L,
    val currentDeathTimeOutetPlayers: MutableList<UserTimeoutConfig> = ArrayList()
)
