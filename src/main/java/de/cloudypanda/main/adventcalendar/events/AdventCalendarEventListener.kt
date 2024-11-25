package de.cloudypanda.main.adventcalendar.events;

import de.cloudypanda.main.Huntcraft
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AdventCalendarEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.sendMessage(Component.text("Welcome to the server! Todays Challenge is as follows:"));

        val adventCalendarConfigModel = huntcraft.adventCalendarConfigManager.readFromFile();

        if (adventCalendarConfigModel.challenges.isEmpty()) {
            e.player.sendMessage(Component.text("No challenges available"));
            return;
        }

        e.player.sendMessage(Component.text(adventCalendarConfigModel.challenges.get(0).message));
    }
}
