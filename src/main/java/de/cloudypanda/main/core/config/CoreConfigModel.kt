package de.cloudypanda.main.core.config;

data class CoreConfigModel(
    val deathTimer: CoreModuleConfig = CoreModuleConfig(),
    val adventCalendar: CoreModuleConfig = CoreModuleConfig(),
    val requestConfig: CoreRequestConfig = CoreRequestConfig(),
    val discordConfig: CoreDiscordConfig = CoreDiscordConfig(),
) {
}
