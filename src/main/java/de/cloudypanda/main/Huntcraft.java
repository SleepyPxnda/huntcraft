package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarLeaderboardCommand;
import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommand;
import de.cloudypanda.main.adventcalendar.config.*;
import de.cloudypanda.main.deathtimer.DeathTimerConfigManager;
import de.cloudypanda.main.common.event.DeathListener;
import de.cloudypanda.main.common.event.JoinListener;
import de.cloudypanda.main.deathtimer.DeathTimerConfigModel;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Huntcraft extends JavaPlugin {
    public final DeathTimerConfigManager deathTimerConfigManager = new DeathTimerConfigManager("hc_deathtimer", this);
    public final AdventCalendarConfigManager adventCalendarConfigManager = new AdventCalendarConfigManager("hc_adventcalendar", this);

    @Override
    public void onEnable() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists();
        adventCalendarConfigManager.createFileIfNotExists();

        deathTimerConfigManager.saveToFile(new DeathTimerConfigModel());

        //Register Event Listener
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("huntcraft-submit", new AdventCalendarSubmitCommand(this));
            commands.register("huntcraft-leaderboard", new AdventCalendarLeaderboardCommand(this));
        });
    }
}
