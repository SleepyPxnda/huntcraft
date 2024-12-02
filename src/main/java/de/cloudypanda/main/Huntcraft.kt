package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarLeaderboardCommand
import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommand
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigManager
import de.cloudypanda.main.core.config.CoreConfigManager
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.core.tablist.TablistManager
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigManager
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class Huntcraft : JavaPlugin() {

    val adventCalendarConfigManager = AdventCalendarConfigManager("hc_adventcalendar", this);
    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this);
    val coreConfigManager = CoreConfigManager("hc_core", this);

    val tablistManager = TablistManager();

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this;
        //Create main Config file
        val coreConfig = coreConfigManager.createFileIfNotExists();

        //Register Core Events
        server.pluginManager.registerEvents(CoreEventListener(), this);

        if (coreConfig.adventCalendar.enabled) {
            registerAdventCalendarModule()
        }

        if (coreConfig.deathTimer.enabled) {
            registerDeathTimerModule()
        }
    }

    private fun registerDeathTimerModule() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists();

        //Register Event Listener
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this);
        this.componentLogger.info("DeathTimer Module is enabled");
    }

    private fun registerAdventCalendarModule() {
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager;

        // Create config file
        adventCalendarConfigManager.createFileIfNotExists();

        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar();
            commands.register(
                "huntcraft-submit",
                "Submit command for the advent calendar",
                listOf("hcs", "submit"),
                AdventCalendarSubmitCommand()
            );
            commands.register(
                "huntcraft-leaderboard",
                "Advent calendar command",
                listOf("hcl"),
                AdventCalendarLeaderboardCommand()
            );
        }

        this.componentLogger.info("Adventcalendar Module is enabled");
    }
}
