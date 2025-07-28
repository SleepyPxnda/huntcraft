package de.cloudypanda.main

import de.cloudypanda.main.config.manager.CoreConfigManager
import de.cloudypanda.main.config.manager.DeathTimerConfigManager
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.core.tablist.TabListManager
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import org.bukkit.plugin.java.JavaPlugin


class Huntcraft : JavaPlugin() {

    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this)
    val coreConfigManager = CoreConfigManager("hc_core", this)

    val tablistManager = TabListManager()

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this
        //Create main Config file
        val coreConfig = coreConfigManager.createFileIfNotExists()

        //Register Core Events
        server.pluginManager.registerEvents(CoreEventListener(), this)

        if (coreConfig.deathTimer.enabled) {
            registerDeathTimerModule()
        }
    }

    private fun registerDeathTimerModule() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists()

        //Register Event Listener
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this)
        this.componentLogger.info("DeathTimer Module is enabled")
    }
}
