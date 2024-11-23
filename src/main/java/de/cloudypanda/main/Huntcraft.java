package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommandListener;
import de.cloudypanda.main.adventcalendar.config.*;
import de.cloudypanda.main.deathtimer.DeathTimerConfigManager;
import de.cloudypanda.main.common.event.DeathListener;
import de.cloudypanda.main.common.event.JoinListener;
import de.cloudypanda.main.deathtimer.DeathTimerConfigModel;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

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
            commands.register("huntcraft-submit", new AdventCalendarSubmitCommandListener(this));
        });

        setupAdventCalendar();
    }

    private void setupAdventCalendar() {
        AdventCalendarConfigModel adventCalendarConfigModel = new AdventCalendarConfigModel();
        // Do something with the config model

        AdventCalendarDayConfig day1Config = new AdventCalendarDayConfig();
        day1Config.setDate("2024-11-24");
        day1Config.setMessage("Welcome to the first day of the Advent Calendar! \n Today's challenge ist to submit a workbench");
        day1Config.setPoints(10);
        day1Config.setItemToSubmit(new AdventCalendarSubmitItemConfig(Material.DIAMOND_SWORD,
                1,
                null,
                null,
                List.of(new AdventCalendarSubmitItemEnchantConfig(Enchantment.SHARPNESS, 5))
        ));

        adventCalendarConfigModel.getChallenges().add(day1Config);

        adventCalendarConfigManager.saveToFile(adventCalendarConfigModel);
    }
}
