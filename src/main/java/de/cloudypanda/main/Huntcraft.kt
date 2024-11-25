package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarLeaderboardCommand
import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommand
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigManager
import de.cloudypanda.main.adventcalendar.events.AdventCalendarEventListener
import de.cloudypanda.main.core.config.CoreConfigManager
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigManager
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class Huntcraft : JavaPlugin() {
    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this);
    val adventCalendarConfigManager = AdventCalendarConfigManager("hc_adventcalendar", this);
    val coreConfigManager = CoreConfigManager("hc_core", this);

    override fun onEnable() {
        //Create main Config file
        coreConfigManager.createFileIfNotExists();

        val coreConfigModel = coreConfigManager.readFromFile();
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager;

        //Register Core Events
        server.pluginManager.registerEvents(CoreEventListener(this), this);

        if (coreConfigModel.adventCalendar.enabled) {
            // Create config file
            adventCalendarConfigManager.createFileIfNotExists();
            server.pluginManager.registerEvents(AdventCalendarEventListener(this), this);

            manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
                val commands: Commands = event.registrar();
                commands.register("huntcraft-submit",
                    "Submit command for the advent calendar",
                    listOf("hcs","submit"),

                    AdventCalendarSubmitCommand(this));
                commands.register("huntcraft-leaderboard",
                    "Open the leaderboard for the advent calendar",
                    listOf("hcl","leaderboard "),
                    AdventCalendarLeaderboardCommand(this));
            }

            this.componentLogger.info("Adventcalendar Module is enabled");
        }

        if (coreConfigModel.deathTimer.enabled) {
            //Create config file
            deathTimerConfigManager.createFileIfNotExists();

            //Register Event Listener
            server.pluginManager.registerEvents(DeathTimerEventListener(this), this);
            this.componentLogger.info("DeathTimer Module is enabled");
        }
    }
}
