package de.cloudypanda

import de.cloudypanda.core.event.CoreEventListener
import de.cloudypanda.database.*
import de.cloudypanda.deathtimer.DeathTimerEventListener
import de.cloudypanda.quest.*
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


class Huntcraft : JavaPlugin() {

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        server.pluginManager.registerEvents(CoreEventListener(), this)
        server.pluginManager.registerEvents(DeathTimerEventListener(), this)
        server.pluginManager.registerEvents(QuestBlockBreakEventListener(), this)
        server.pluginManager.registerEvents(QuestBlockPlaceEventListener(), this)
        server.pluginManager.registerEvents(QuestEntityKillEventListener(), this)
        server.pluginManager.registerEvents(QuestItemCraftEventHandler(), this)
        server.pluginManager.registerEvents(QuestAchievementEventListener(), this)


        registerCommand("quest", QuestCommand())

        val host = config.get("database.host") as String
        val port = config.get("database.port") as Int
        val database = config.get("database.database") as String
        val username = config.get("database.username") as String
        val password = config.get("database.password") as String

        Database.connect("jdbc:pgsql://${host}:${port}/${database}", driver = "com.impossibl.postgres.jdbc.PGDriver", username, password)

        transaction {
            SchemaUtils.create(
                PlayerTable, QuestTable, CompletedQuestTable, QuestProgressTable, PlayerSessionTable
            )
        }

    }

    override fun onDisable() {
    }
}
