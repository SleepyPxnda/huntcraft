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

public final class Huntcraft extends JavaPlugin {
    public final DeathTimerConfigManager deathTimerConfigManager = new DeathTimerConfigManager("hc_deathtimer", this);
    public final AdventCalendarConfigManager adventCalendarConfigManager = new AdventCalendarConfigManager("hc_adventcalendar", this);
    public final CoreConfigManager coreConfigManager = new CoreConfigManager("hc_core", this);

    @Override
    public void onEnable() {
        //Create main Config file
        coreConfigManager.createFileIfNotExists();

        CoreConfigModel coreConfigModel = coreConfigManager.readFromFile();
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();

        if(coreConfigModel.adventCalendar.enabled){
            // Create config file
            adventCalendarConfigManager.createFileIfNotExists();
            getServer().getPluginManager().registerEvents(new AdventCalendarEventListener(this), this);

            manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
                final Commands commands = event.registrar();
                commands.register("huntcraft-submit", new AdventCalendarSubmitCommand(this));
                commands.register("huntcraft-leaderboard", new AdventCalendarLeaderboardCommand(this));
            });
            this.getComponentLogger().info("Adventcalendar Module is enabled");
        }

        if(coreConfigModel.deathTimer.enabled){
            //Create config file
            deathTimerConfigManager.createFileIfNotExists();

            //Register Event Listener
            getServer().getPluginManager().registerEvents(new DeathTimerEventListener(this), this);
            this.getComponentLogger().info("DeathTimer Module is enabled");
        }
    }
}
