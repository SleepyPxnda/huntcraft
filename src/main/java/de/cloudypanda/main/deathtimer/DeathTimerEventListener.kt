package de.cloudypanda.main.deathtimer;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel;
import de.cloudypanda.main.deathtimer.config.DeathTimerConfigModel;
import de.cloudypanda.main.deathtimer.config.UserTimeoutConfig;
import de.cloudypanda.main.util.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Instant;
import java.util.Optional;

public class DeathTimerEventListener implements Listener {

    private final Huntcraft huntcraft;

    public DeathTimerEventListener(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e){
        DeathTimerConfigModel model = huntcraft.deathTimerConfigManager.readFromFile();
        Instant dateOfDeath = Instant.now();
        boolean isPlayerInList = model.currentDeathTimeOutetPlayers.stream()
                .anyMatch(x -> x.getPlayerUUID().equals(e.getPlayer().getUniqueId()));

        if(isPlayerInList){
            model.currentDeathTimeOutetPlayers.removeIf(x -> x.getPlayerUUID().equals(e.getPlayer().getUniqueId()));
        }

        model.currentDeathTimeOutetPlayers.add(
                new UserTimeoutConfig(e.getPlayer().getUniqueId(),
                                dateOfDeath.toEpochMilli(),
                                e.getPlayer().getName()));

        huntcraft.deathTimerConfigManager.saveToFile(model);

        try {
            //WebhookManager.sendDeathMessage(e.getDeathMessage());
        }catch (Exception ex){
            huntcraft.getComponentLogger().error("Error sending death message to webhook. {}", ex.getMessage());
        }
    }

    @EventHandler
    public void onPostRespawnEvent(PlayerPostRespawnEvent e){
        DeathTimerConfigModel model = huntcraft.deathTimerConfigManager.readFromFile();

        Optional<UserTimeoutConfig> presentPlayer = model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.getPlayerUUID().equals(e.getPlayer().getUniqueId()))
                .findFirst();

        if(presentPlayer.isPresent() && presentPlayer.get().isAllowedToJoin(model.deathTimeout)){
            return;
        }

        Instant dateOfDeath = Instant.ofEpochMilli(model.currentDeathTimeOutetPlayers
                .stream()
                .filter(x -> x.getPlayerUUID().equals(e.getPlayer().getUniqueId()))
                .findFirst()
                .get().getLatestDeath());

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

        UserTimeoutConfig userConfig = model.currentDeathTimeOutetPlayers
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
