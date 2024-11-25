package de.cloudypanda.main.deathtimer;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.core.integrations.discord.WebhookManager
import de.cloudypanda.main.core.integrations.rest.RequestManager
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
import java.time.LocalDate

class DeathTimerEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {
        val model = huntcraft.deathTimerConfigManager.readFromFile();
        val dateOfDeath = Instant.now();
        val isPlayerInList = model.currentDeathTimeOutetPlayers.stream()
            .anyMatch { x -> x.playerUUID == e.player.uniqueId };

        if (isPlayerInList) {
            model.currentDeathTimeOutetPlayers.removeIf({ x -> x.playerUUID == e.player.uniqueId });
        }

        model.currentDeathTimeOutetPlayers.add(
            UserTimeoutConfig(
                e.player.uniqueId,
                dateOfDeath.toEpochMilli(),
                e.player.name
            )
        );

        huntcraft.deathTimerConfigManager.saveToFile(model);

        WebhookManager.sendDeathMessage("${e.deathMessage()}");
        RequestManager().updatePlayerDeath(e.player.uniqueId, dateOfDeath.toEpochMilli());
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val model = huntcraft.deathTimerConfigManager.readFromFile();

        val presentPlayer = model.currentDeathTimeOutetPlayers
            .stream()
            .filter { x -> x.playerUUID == e.player.uniqueId }
            .findFirst();

        if (presentPlayer.isPresent && presentPlayer.get().isAllowedToJoin(model.deathTimeout)) {
            return;
        }

        val dateOfDeath = Instant.ofEpochMilli(
            model.currentDeathTimeOutetPlayers
            .stream()
            .filter { x -> x.playerUUID == e.player.uniqueId }
            .findFirst()
            .get().latestDeath);

        val date = DateUtil.getFormattedStringForDateAfterMillis(dateOfDeath.toEpochMilli(), model.deathTimeout);
        val timeout = DateUtil.getFormattedDurationUntilJoin(0, model.deathTimeout);

        huntcraft.componentLogger.info(
            String.format(
                "'%s' died. '%s' can join again after %s",
                e.player.name,
                e.player.name,
                date
            )
        );

        val message = Component.text("Due to the rules of 'Huntcraft' \n you were dispelled from the server for: \n\n")
            .append(Component.text(timeout, TextColor.color(255, 0, 0)))
            .append(Component.text("\n\n\n", TextColor.color(255, 255, 255)))
            .append(Component.text("Read more about the rules in our discord"))
            .append(Component.text());
        e.player.kick(message);
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val model = huntcraft.deathTimerConfigManager.readFromFile();

        val isPlayerInList = model.currentDeathTimeOutetPlayers
            .any { x -> x.playerUUID == e.uniqueId };

        if (!isPlayerInList) {
            e.allow();
            return;
        }

        val userConfig = model.currentDeathTimeOutetPlayers
            .find { x -> x.playerUUID == e.uniqueId };

        if (userConfig == null) {
            e.allow();
            return;
        }

        if (userConfig.isAllowedToJoin(model.deathTimeout)) {
            e.allow();
            return;
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(userConfig.latestDeath, model.deathTimeout);
        val message = Component.text("You died. \n", TextColor.color(255, 0, 0))
            .append(Component.text("You can't rejoin until $date. \n\n", TextColor.color(255, 255, 255)))
            .append(Component.text("Time until rejoin is possible: \n", TextColor.color(255, 255, 255)))
            .append(
                Component.text(
                    DateUtil.getFormattedDurationUntilJoin(
                        Instant.now().toEpochMilli(), userConfig.latestDeath, model.deathTimeout
                    ), TextColor.color(124, 252, 0)
                )
            );

        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.sendMessage(Component.text("Welcome to the server! Todays Challenge is as follows:"));

        val adventCalendarConfigModel = Huntcraft.adventCalendarConfig;

        if (adventCalendarConfigModel.challenges.isEmpty()) {
            e.player.sendMessage(Component.text("No challenges available"));
            return;
        }

        val dayConfig = adventCalendarConfigModel.getConfigForDay(LocalDate.now());

        if (dayConfig == null) {
            e.player.sendMessage(Component.text("No challenges available"));
            return;
        }

        e.player.sendMessage(Component.text(dayConfig.message));
    }
}
