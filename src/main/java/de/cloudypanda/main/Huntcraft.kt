package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarLeaderboardCommand;
import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommand;
import de.cloudypanda.main.adventcalendar.config.*;
import de.cloudypanda.main.core.config.CoreConfigManager;
import de.cloudypanda.main.core.config.CoreConfigModel;
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigManager;
import de.cloudypanda.main.deathtimer.DeathTimerEventListener;
import de.cloudypanda.main.adventcalendar.events.AdventCalendarEventListener;
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigModel;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

class Huntcraft : JavaPlugin () {
    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this);
    val adventCalendarConfigManager = AdventCalendarConfigManager("hc_adventcalendar", this);
    val coreConfigManager = CoreConfigManager("hc_core", this);

    override fun onEnable() {
        //Create main Config file
        coreConfigManager.createFileIfNotExists();

        val coreConfigModel = coreConfigManager.readFromFile();
        val manager: LifecycleEventManager<Plugin> = this.getLifecycleManager();

        if(coreConfigModel.adventCalendar.enabled){
            // Create config file
            adventCalendarConfigManager.createFileIfNotExists();
            getServer().getPluginManager().registerEvents(AdventCalendarEventListener(this), this);

            manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
                val commands: Commands = event.registrar();
                commands.register("huntcraft-submit", AdventCalendarSubmitCommand(this));
                commands.register("huntcraft-leaderboard", AdventCalendarLeaderboardCommand(this));
            }

            this.getComponentLogger().info("Adventcalendar Module is enabled");
        }

        if(coreConfigModel.deathTimer.enabled){
            //Create config file
            deathTimerConfigManager.createFileIfNotExists();

            //Register Event Listener
            getServer().getPluginManager().registerEvents(DeathTimerEventListener(this), this);
            this.getComponentLogger().info("DeathTimer Module is enabled");
        }
    }
}
