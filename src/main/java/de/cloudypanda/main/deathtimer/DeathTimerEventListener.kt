package de.cloudypanda.main.deathtimer

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.HuntcraftConfig
import de.cloudypanda.main.core.integrations.discord.WebhookNotificationManager
import de.cloudypanda.main.util.DateUtil
import de.cloudypanda.main.util.TextUtil
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import java.time.Instant

class DeathTimerEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death
        val dateOfDeath = Instant.now()
        val isPlayerInList = deathTimerConfig.playerTimeouts.stream().anyMatch { x -> x.playerUUID == e.player.uniqueId }

        if (isPlayerInList) {
            deathTimerConfig.playerTimeouts.removeIf { x -> x.playerUUID == e.player.uniqueId }
        }

        deathTimerConfig.playerTimeouts.add(
            HuntcraftConfig.UserTimeoutConfig(
                e.player.uniqueId,
                dateOfDeath.toEpochMilli(),
                e.player.name
            )
        )

        huntcraft.configManager.setValue(
            listOf("death", "playerTimeouts"),
            deathTimerConfig.playerTimeouts
        )

        val deathMessage = PlainTextComponentSerializer.plainText().serialize(e.deathMessage()!!)
        WebhookNotificationManager().sendDeathMessage(deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val optionalPlayer = deathTimerConfig.playerTimeouts
            .stream()
            .filter { x -> x.playerUUID == e.player.uniqueId }
            .findFirst()

        if (optionalPlayer.isPresent && optionalPlayer.get().isAllowedToJoin(deathTimerConfig.deathTimer)) {
            return
        }

        val timeout = DateUtil.getFormattedDurationUntilJoin(0,deathTimerConfig.deathTimer)
        val message = TextUtil.getDeathTimerKickMessage(timeout)
        e.player.kick(message)

        val date = DateUtil.getFormattedStringForDateAfterMillis(optionalPlayer.get().latestDeath, deathTimerConfig.deathTimer)
        Huntcraft.instance.server.sendMessage(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))
        println(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death


        val isPlayerInList = deathTimerConfig.playerTimeouts
            .any { x -> x.playerUUID == e.uniqueId }

        if (!isPlayerInList) {
            e.allow()
            return
        }

        val userConfig = deathTimerConfig.playerTimeouts
            .find { x -> x.playerUUID == e.uniqueId }

        if (userConfig == null) {
            e.allow()
            return
        }

        if (userConfig.isAllowedToJoin(deathTimerConfig.deathTimer)) {
            e.allow()
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(userConfig.latestDeath, deathTimerConfig.deathTimer)
        val message = TextUtil.getDeathTimerTimeoutMessage(date, userConfig.latestDeath, deathTimerConfig.deathTimer)
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
    }
}
