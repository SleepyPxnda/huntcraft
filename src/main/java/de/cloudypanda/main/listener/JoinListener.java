package de.cloudypanda.main.listener;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.timeout.UserTimeout;
import de.cloudypanda.main.util.ConfigModel;
import de.cloudypanda.main.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;

public class JoinListener implements Listener {

    private final Huntcraft huntcraft;

    public JoinListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e){
        ConfigModel model = huntcraft.configManager.readFromFile();
        boolean isPlayerInList = model.currentDeathTimeOutetPlayers
                        .stream()
                        .anyMatch(x -> x.playerUUID
                        .equals(e.getUniqueId()));

        if(!isPlayerInList){
            e.allow();
            return;
        }

        UserTimeout userConfig = model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.playerUUID.equals(e.getUniqueId()))
                .findFirst()
                .get();

        if(Instant.now().isAfter(Instant.ofEpochMilli(userConfig.latestDeath + model.deathTimeout))){
            e.allow();
            return;
        }

        UserTimeout user = model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getUniqueId())).findFirst().get();
        String date = DateUtil.getFormattedStringForDateAfterMillis(user.latestDeath, model.getDeathTimeout());
        Component message = Component.text("You died. \n", TextColor.color(255, 0,0))
                .append(Component.text("You can't rejoin until " + date + ". \n\n", TextColor.color(255, 255, 255)))
                .append(Component.text("Time until rejoin is possible: \n", TextColor.color(255, 255, 255)))
                .append(Component.text(DateUtil.getFormattedDurationUntilJoin(Instant.now().toEpochMilli()
                        ,user.getLatestDeath(), model.getDeathTimeout()), TextColor.color(124,252,0)));

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
    }

}
