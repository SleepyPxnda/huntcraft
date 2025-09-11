package de.cloudypanda.main

import de.cloudypanda.main.config.ConfigManager
import de.cloudypanda.main.config.HuntcraftConfig
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import de.cloudypanda.main.quest.*
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

        logger.info { "Plugin found!" }

        configManager.loadConfig()

        logger.info { "Configs loaded!" }

        QuestSystem.load()

        logger.info { "Quests prepared!" }

        server.pluginManager.registerEvents(CoreEventListener(), this)
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this)
        server.pluginManager.registerEvents(QuestBlockBreakEventListener(), this)
        server.pluginManager.registerEvents(QuestBlockPlaceEventListener(), this)
        server.pluginManager.registerEvents(QuestEntityKillEventListener(), this)
        server.pluginManager.registerEvents(QuestItemCraftEventHandler(), this)

        logger.info { "Events registered!" }

        registerCommand("quest", QuestCommand())

        logger.info { "Commands registered!" }
    }

    override fun onDisable() {
        configManager.saveConfig()
        QuestSystem.save()
        logger.info { "Configs saved!" }
    }
}
