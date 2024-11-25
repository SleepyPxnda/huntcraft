package de.cloudypanda.main.adventcalendar.events;

import de.cloudypanda.main.Huntcraft
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.time.LocalDate

class AdventCalendarEventListener() : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.sendMessage(Component.text("Welcome to the server! Todays Challenge is as follows:"));

        val adventCalendarConfigModel = Huntcraft.instance.adventCalendarConfig

        if (adventCalendarConfigModel.challenges.isEmpty()) {
            e.player.sendMessage(Component.text("No challenges available"));
            return;
        }

        val challenge = adventCalendarConfigModel.getConfigForDay(LocalDate.now());

        e.player.sendMessage(Component.text(challenge?.message ?: "No challenge available"));
    }
}
