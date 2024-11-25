package de.cloudypanda.main.deathtimer;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.core.integrations.discord.WebhookManager
import de.cloudypanda.main.core.integrations.rest.RequestManager
import de.cloudypanda.main.deathtimer.config.UserTimeoutConfig
import de.cloudypanda.main.util.DateUtil
import de.cloudypanda.main.util.TextUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.time.Instant

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

        val timeout = DateUtil.getFormattedDurationUntilJoin(0, model.deathTimeout);
        val message = TextUtil.getDeathTimerKickMessage(timeout);
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
        val message = TextUtil.getDeathTimerTimeoutMessage(date, userConfig.latestDeath, model.deathTimeout);
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
    }
}
