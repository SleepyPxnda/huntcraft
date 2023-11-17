package de.cloudypanda.main.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.timeout.UserTimeout;
import de.cloudypanda.main.util.ConfigModel;
import de.cloudypanda.main.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Instant;

public class DeathListener implements Listener {

    private final Huntcraft huntcraft;

    public DeathListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e){
        ConfigModel model = huntcraft.configManager.readFromFile();
        Instant dateOfDeath = Instant.now();
        if(!model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getPlayer().getUniqueId())).findFirst().isEmpty()){
            model.currentDeathTimeOutetPlayers.removeIf(x -> x.playerUUID.equals(e.getPlayer().getUniqueId()));
        }
        model.currentDeathTimeOutetPlayers.add(new UserTimeout(e.getPlayer().getUniqueId(), dateOfDeath.toEpochMilli()));
        huntcraft.configManager.saveToFile(model);
    }

    @EventHandler
    public void onPostRespawnEvent(PlayerPostRespawnEvent e){
        ConfigModel model = huntcraft.configManager.readFromFile();
        Instant dateOfDeath = Instant.ofEpochMilli(model.currentDeathTimeOutetPlayers.stream().filter(x -> x.playerUUID.equals(e.getPlayer().getUniqueId())).findFirst().get().latestDeath);
        String date = DateUtil.getFormattedStringForDateAfterMillis(dateOfDeath.toEpochMilli(), model.getDeathTimeout());
        String timeout = DateUtil.getFormattedDurationUntilJoin(0, model.deathTimeout);

        huntcraft.getComponentLogger().info(String.format("'%s' died. '%s' can join again after %s",
                e.getPlayer().getName(),
                e.getPlayer().getName(),
                date));

        Component message = Component.text("Due to the rules of 'Huntcraft' \n you were dispelled from the server for: \n\n")
                        .append(Component.text(timeout, TextColor.color(255,0,0)));
        e.getPlayer().kick(message);
    }
}
