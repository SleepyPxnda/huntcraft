package de.cloudypanda.main.common.event;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel;
import de.cloudypanda.main.deathtimer.UserTimeout;
import de.cloudypanda.main.deathtimer.DeathTimerConfigModel;
import de.cloudypanda.main.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;

public class JoinListener implements Listener {

    private final Huntcraft huntcraft;

    public JoinListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e){
        DeathTimerConfigModel model = huntcraft.deathTimerConfigManager.readFromFile();

        boolean isPlayerInList = model.currentDeathTimeOutetPlayers
                        .stream()
                        .anyMatch(x -> x.getPlayerUUID().equals(e.getUniqueId()));

        if(!isPlayerInList){
            e.allow();
            return;
        }

        UserTimeout userConfig = model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.getPlayerUUID().equals(e.getUniqueId()))
                .findFirst()
                .get();

        if(userConfig.isAllowedToJoin(model.deathTimeout)){
            e.allow();
            return;
        }

        String date = DateUtil.getFormattedStringForDateAfterMillis(userConfig.getLatestDeath(), model.getDeathTimeout());
        Component message = Component.text("You died. \n", TextColor.color(255, 0,0))
                .append(Component.text("You can't rejoin until " + date + ". \n\n", TextColor.color(255, 255, 255)))
                .append(Component.text("Time until rejoin is possible: \n", TextColor.color(255, 255, 255)))
                .append(Component.text(DateUtil.getFormattedDurationUntilJoin(Instant.now().toEpochMilli()
                        ,userConfig.getLatestDeath(), model.getDeathTimeout()), TextColor.color(124,252,0)));

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
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
