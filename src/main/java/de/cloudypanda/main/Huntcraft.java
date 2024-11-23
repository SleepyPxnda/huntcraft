package de.cloudypanda.main;

import de.cloudypanda.main.file.DeathTimerConfigManager;
import de.cloudypanda.main.listener.DeathListener;
import de.cloudypanda.main.listener.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Huntcraft extends JavaPlugin {
    public final DeathTimerConfigManager deathTimerConfigManager = new DeathTimerConfigManager("hc_deathtimer", this);

    @Override
    public void onEnable() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists();

        //Register Event Listener
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
    }
}
