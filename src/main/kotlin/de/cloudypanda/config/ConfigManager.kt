package de.cloudypanda.config

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ConfigManager<T : Any>(
    private val configPath: Path,
    private val configClass: Class<T>,
    private val defaultConfig: T,
    private val json: Json = Json { ignoreUnknownKeys = true; prettyPrint = true }
) {
    var config: T = defaultConfig
        private set

    @OptIn(InternalSerializationApi::class)
    fun loadConfig() {
        if (!configPath.exists()) {
            saveConfig(defaultConfig)
        }
        try {
            val text = configPath.readText()
            config = json.decodeFromString(configClass.kotlin.serializer(), text)
        } catch (e: Exception) {
            e.printStackTrace()
            config = defaultConfig
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun saveConfig(configToSave: T = config) {
        try {
            val text = json.encodeToString(configClass.kotlin.serializer(), configToSave)
            configPath.writeText(text)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun setValue(key: Iterable<String>, value: Any) {
        // This is a simple implementation: load the config as a map, update, and save.
        // For complex/nested updates, a more robust solution is needed.
        loadConfig()
        val map = json.decodeFromString<MutableMap<String, Any>>(json.encodeToString(configClass.kotlin.serializer(), config))
        var current: MutableMap<String, Any> = map
        val keys = key.toList()
        for (i in 0 until keys.size - 1) {
            val k = keys[i]
            if (current[k] !is MutableMap<*, *>) {
                current[k] = mutableMapOf<String, Any>()
            }
            current = current[k] as MutableMap<String, Any>
        }
        current[keys.last()] = value
        val updatedJson = json.encodeToString(map)
        configPath.writeText(updatedJson)
        loadConfig()
    }
}