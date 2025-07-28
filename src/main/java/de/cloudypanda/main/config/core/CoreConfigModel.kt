package de.cloudypanda.main.config.core

data class CoreConfigModel(
    val deathTimer: CoreModuleConfig = CoreModuleConfig(),
    val webhook: CoreDiscordConfig = CoreDiscordConfig(),
    val infos: CoreInfoConfig = CoreInfoConfig()
)
