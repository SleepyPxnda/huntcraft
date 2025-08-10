package de.cloudypanda.main.config

import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.jackson.JacksonConfigurationLoader
import java.nio.file.Path
import kotlin.io.path.exists

class ConfigManager<T : Any>(
    private val configPath: Path,
    private val configClass: Class<T>,
    private val defaultConfig: T
) {
    private val loader = JacksonConfigurationLoader.builder()
        .path(configPath)
        .build()

    var config: T = defaultConfig
        private set

    fun loadConfig() {
        if (!configPath.exists()) {
            saveConfig(defaultConfig)
        }
        val node: ConfigurationNode = loader.load()
        config = node.get(configClass) ?: defaultConfig
    }

    fun saveConfig(configToSave: T = config) {
        val node = loader.createNode()
        node.set(configClass, configToSave)
        loader.save(node)
    }

    fun setValue(key: Iterable<String>, value: Any) {
        val node: ConfigurationNode = loader.load()
        node.node(key).set(value)
        loader.save(node)
    }
}