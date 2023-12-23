package de.cloudypanda.main.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.integrations.WebhookManager;
import de.cloudypanda.main.timeout.UserTimeout;
import de.cloudypanda.main.util.ConfigModel;
import de.cloudypanda.main.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Instant;
import java.util.Optional;

public class DeathListener implements Listener {

    private final Huntcraft huntcraft;

    public DeathListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e){
        ConfigModel model = huntcraft.configManager.readFromFile();
        Instant dateOfDeath = Instant.now();
        boolean isPlayerInList = model.currentDeathTimeOutetPlayers.stream()
                .anyMatch(x -> x.playerUUID.equals(e.getPlayer().getUniqueId()));

        if(isPlayerInList){
            model.currentDeathTimeOutetPlayers.removeIf(x -> x.playerUUID.equals(e.getPlayer().getUniqueId()));
        }

        model.currentDeathTimeOutetPlayers.add(
                new UserTimeout(e.getPlayer().getUniqueId(),
                                dateOfDeath.toEpochMilli(),
                                e.getPlayer().getName()));
        huntcraft.configManager.saveToFile(model);
        WebhookManager.sendDeathMessage(e.getDeathMessage());
    }

    @EventHandler
    public void onPostRespawnEvent(PlayerPostRespawnEvent e){
        ConfigModel model = huntcraft.configManager.readFromFile();

        Optional<UserTimeout> presentPlayer = model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.getPlayerUUID().equals(e.getPlayer().getUniqueId()))
                .findFirst();

        if(presentPlayer.isPresent() && presentPlayer.get().isAllowedToJoin(model.deathTimeout)){
            return;
        }

        Instant dateOfDeath = Instant.ofEpochMilli(model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.playerUUID.equals(e.getPlayer().getUniqueId()))
                .findFirst()
                .get().latestDeath);

        String date = DateUtil.getFormattedStringForDateAfterMillis(dateOfDeath.toEpochMilli(), model.getDeathTimeout());
        String timeout = DateUtil.getFormattedDurationUntilJoin(0, model.deathTimeout);

        huntcraft.getComponentLogger().info(String.format("'%s' died. '%s' can join again after %s",
                e.getPlayer().getName(),
                e.getPlayer().getName(),
                date));

        Component message = Component.text("Due to the rules of 'Huntcraft' \n you were dispelled from the server for: \n\n")
                .append(Component.text(timeout, TextColor.color(255,0,0)))
                .append(Component.text("\n\n\n", TextColor.color(255,255,255)))
                .append(Component.text("Read more about the rules in our discord"))
                .append(Component.text());
        e.getPlayer().kick(message);
    }
}
