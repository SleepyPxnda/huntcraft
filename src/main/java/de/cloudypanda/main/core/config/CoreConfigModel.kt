package de.cloudypanda.main.core.config;

data class CoreConfigModel(
    val deathTimer: CoreModuleConfig = CoreModuleConfig(),
    val adventCalendar: CoreModuleConfig = CoreModuleConfig(),
    val requests: CoreRequestConfig = CoreRequestConfig(),
    val webhook: CoreDiscordConfig = CoreDiscordConfig(),
    val infos: CoreInfoConfig = CoreInfoConfig()
) {
}
