package de.cloudypanda.main.config.core

data class CoreConfigModel(
    val deathTimer: CoreModuleConfig = CoreModuleConfig(),
    val requests: CoreRequestConfig = CoreRequestConfig(),
    val webhook: CoreDiscordConfig = CoreDiscordConfig(),
    val infos: CoreInfoConfig = CoreInfoConfig()
)
