package de.cloudypanda.main;

import de.cloudypanda.main.file.FileManager;
import de.cloudypanda.main.listener.DeathListener;
import de.cloudypanda.main.listener.JoinListener;
import de.cloudypanda.main.util.ConfigModel;
import org.bukkit.plugin.java.JavaPlugin;

public final class Huntcraft extends JavaPlugin {
    public static final FileManager configManager = new FileManager("huntcraft_config");

    @Override
    public void onEnable() {
        //Create config file
        configManager.createFileIfExists();
        //Register Event Listener
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
