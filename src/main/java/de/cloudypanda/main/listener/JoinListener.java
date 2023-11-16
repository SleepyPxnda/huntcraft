package de.cloudypanda.main.listener;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.timeout.UserTimeout;
import de.cloudypanda.main.util.ConfigModel;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class JoinListener implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent e){
        ConfigModel model = Huntcraft.configManager.readFromFile();
        String pattern = "yyyy-MM-dd HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


        if(model.currentDeathTimeOutetPlayers.stream().anyMatch(x -> x.playerUUID.equals(e.getUniqueId()))){
            UserTimeout user = model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getUniqueId())).findFirst().get();
            String date = simpleDateFormat.format(Date.from(Instant.ofEpochMilli(user.latestDeath).plusMillis(model.getDeathTimeout())));
            Component message = Component.text("You died. \n You can't rejoin until " + date + ".");
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);
        }
    }

}
