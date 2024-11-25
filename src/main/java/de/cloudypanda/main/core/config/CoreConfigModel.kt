package de.cloudypanda.main.core.config;

data class CoreConfigModel(
    val deathTimer: CoreModuleConfig = CoreModuleConfig(),
    val adventCalendar: CoreModuleConfig = CoreModuleConfig(),
    val huntCraftBackendApiKey: String = ""
) {
}
