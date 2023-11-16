package de.cloudypanda.main.listener;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.timeout.UserTimeout;
import de.cloudypanda.main.util.ConfigModel;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e){
        ConfigModel model = Huntcraft.configManager.readFromFile();
        Instant dateOfDeath = Instant.now();
        if(!model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getPlayer().getUniqueId())).findFirst().isEmpty()){
            model.currentDeathTimeOutetPlayers.removeIf(x -> x.playerUUID.equals(e.getPlayer().getUniqueId()));
        }
        model.currentDeathTimeOutetPlayers.add(new UserTimeout(e.getPlayer().getUniqueId(), dateOfDeath.toEpochMilli()));
        Huntcraft.configManager.saveToFile(model);
    }

    @EventHandler
    public void onRespawnEvent(PlayerRespawnEvent e){
        ConfigModel model = Huntcraft.configManager.readFromFile();
        Instant dateOfDeath = Instant.ofEpochMilli(model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getPlayer().getUniqueId())).findFirst().get().latestDeath);
        String pattern = "yyyy-MM-dd HH-mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(Date.from(dateOfDeath.plusMillis(model.getDeathTimeout())));
        System.out.printf("'%s' died. '%s' can join again after %s%n",
                e.getPlayer().getName(),
                e.getPlayer().getName(),
                date);
        e.getPlayer().kick(Component.text("Due to the rules of 'Huntcraft' you are dispelled from the server for 24 hours."));
    }
}
