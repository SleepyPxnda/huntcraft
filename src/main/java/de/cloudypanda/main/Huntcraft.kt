package de.cloudypanda.main

import de.cloudypanda.main.config.ConfigManager
import de.cloudypanda.main.config.HuntcraftConfig
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import org.bukkit.plugin.java.JavaPlugin
import kotlin.io.path.Path


class Huntcraft : JavaPlugin() {
    var configManager: ConfigManager<HuntcraftConfig> = ConfigManager(
        configPath = Path("huntcraft-config.json"),
        configClass = HuntcraftConfig::class.java,
        defaultConfig = HuntcraftConfig()
    )

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this
        configManager.loadConfig()

        server.pluginManager.registerEvents(CoreEventListener(), this)
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this)
    }
}
