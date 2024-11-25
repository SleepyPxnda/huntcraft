package de.cloudypanda.main;

import de.cloudypanda.main.adventcalendar.command.AdventCalendarSubmitCommand
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigManager
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel
import de.cloudypanda.main.core.config.CoreConfigManager
import de.cloudypanda.main.core.config.CoreConfigModel
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.core.tablist.TablistManager
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigManager
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigModel
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Huntcraft : JavaPlugin() {

    val adventCalendarConfigManager = AdventCalendarConfigManager("hc_adventcalendar", this);
    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this);

    private val coreConfigManager = CoreConfigManager("hc_core", this);

    var adventCalendarConfig = AdventCalendarConfigModel();
    var coreConfigModel = CoreConfigModel();
    var deathTimerConfigModel = DeathTimerConfigModel();

    val tablistManager = TablistManager();

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this;
        //Create main Config file
        coreConfigManager.createFileIfNotExists();
        coreConfigModel = coreConfigManager.readFromFile();

        //Register Core Events
        server.pluginManager.registerEvents(CoreEventListener(), this);

        if (coreConfigModel.adventCalendar.enabled) {
            registerAdventCalendarModule()
        }

        if (coreConfigModel.deathTimer.enabled) {
            registerDeathTimerModule()
        }
    }

    private fun registerDeathTimerModule() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists();
        deathTimerConfigModel = deathTimerConfigManager.readFromFile();

        //Register Event Listener
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this);
        this.componentLogger.info("DeathTimer Module is enabled");
    }

    private fun registerAdventCalendarModule() {
        val manager: LifecycleEventManager<Plugin> = this.lifecycleManager;

        // Create config file
        adventCalendarConfigManager.createFileIfNotExists();
        adventCalendarConfig = adventCalendarConfigManager.readFromFile();

        manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val commands: Commands = event.registrar();
            commands.register(
                "huntcraft-submit",
                "Submit command for the advent calendar",
                listOf("hcs", "submit"),
                AdventCalendarSubmitCommand()
            );
        }

        this.componentLogger.info("Adventcalendar Module is enabled");
    }
}
