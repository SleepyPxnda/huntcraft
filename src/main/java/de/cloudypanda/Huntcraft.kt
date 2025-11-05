package de.cloudypanda

import de.cloudypanda.config.ConfigManager
import de.cloudypanda.config.HuntcraftConfig
import de.cloudypanda.core.event.CoreEventListener
import de.cloudypanda.database.*
import de.cloudypanda.deathtimer.DeathTimerEventListener
import de.cloudypanda.quest.*
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
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

        server.pluginManager.registerEvents(CoreEventListener(), this)
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this)
        server.pluginManager.registerEvents(QuestBlockBreakEventListener(), this)
        server.pluginManager.registerEvents(QuestBlockPlaceEventListener(), this)
        server.pluginManager.registerEvents(QuestEntityKillEventListener(), this)
        server.pluginManager.registerEvents(QuestItemCraftEventHandler(), this)
        server.pluginManager.registerEvents(QuestAchievementEventListener(), this)

        logger.info { "Events registered!" }

        registerCommand("quest", QuestCommand())

        logger.info { "Commands registered!" }

        Database.connect("jdbc:h2:file:./plugins/huntcraft/huntcraft;AUTO_SERVER=TRUE", driver = "org.h2.Driver")

        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                PlayerTable, QuestTable, CompletedQuestTable, QuestProgressTable, PlayerSessionTable
            )
        }

        logger.info { "Database connected and tables created!" }
    }

    override fun onDisable() {
        configManager.saveConfig()
        logger.info { "Configs saved!" }
    }
}
