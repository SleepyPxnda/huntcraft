package de.cloudypanda.main.adventcalendar.events;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class AdventCalendarEventListener implements Listener {

    private final Huntcraft huntcraft;

    public AdventCalendarEventListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        e.getPlayer().sendMessage(Component.text("Welcome to the server! Todays Challenge is as follows:"));

        AdventCalendarConfigModel adventCalendarConfigModel = huntcraft.adventCalendarConfigManager.readFromFile();

        if(adventCalendarConfigModel.getChallenges().isEmpty()){
            e.getPlayer().sendMessage(Component.text("No challenges available"));
            return;
        }

        e.getPlayer().sendMessage(Component.text(adventCalendarConfigModel.getChallenges().get(0).getMessage()));
    }
}
