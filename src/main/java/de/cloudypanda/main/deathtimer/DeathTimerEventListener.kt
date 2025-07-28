package de.cloudypanda.main.deathtimer

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.core.integrations.discord.WebhookManager
import de.cloudypanda.main.config.deathtimer.UserTimeoutConfig
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
        val deathTimerConfig = huntcraft.deathTimerConfigManager.readFromFile()
        val dateOfDeath = Instant.now()
        val isPlayerInList = deathTimerConfig.currentDeathTimeOutetPlayers.stream()
            .anyMatch { x -> x.playerUUID == e.player.uniqueId }

        if (isPlayerInList) {
            deathTimerConfig.currentDeathTimeOutetPlayers.removeIf({ x -> x.playerUUID == e.player.uniqueId })
        }

        deathTimerConfig.currentDeathTimeOutetPlayers.add(
            UserTimeoutConfig(
                e.player.uniqueId,
                dateOfDeath.toEpochMilli(),
                e.player.name
            )
        )

        huntcraft.deathTimerConfigManager.saveToFile(deathTimerConfig)

        val deathMessage = PlainTextComponentSerializer.plainText().serialize(e.deathMessage()!!)
        WebhookManager.sendDeathMessage(deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val model = huntcraft.deathTimerConfigManager.readFromFile()

        val presentPlayer = model.currentDeathTimeOutetPlayers
            .stream()
            .filter { x -> x.playerUUID == e.player.uniqueId }
            .findFirst()

        if (presentPlayer.isPresent && presentPlayer.get().isAllowedToJoin(model.deathTimeout)) {
            return
        }

        val timeout = DateUtil.getFormattedDurationUntilJoin(0, model.deathTimeout)
        val message = TextUtil.getDeathTimerKickMessage(timeout)
        e.player.kick(message)

        val date = DateUtil.getFormattedStringForDateAfterMillis(presentPlayer.get().latestDeath, model.deathTimeout)
        Huntcraft.instance.server.sendMessage(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))
        println(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val model = huntcraft.deathTimerConfigManager.readFromFile()

        val isPlayerInList = model.currentDeathTimeOutetPlayers
            .any { x -> x.playerUUID == e.uniqueId }

        if (!isPlayerInList) {
            e.allow()
            return
        }

        val userConfig = model.currentDeathTimeOutetPlayers
            .find { x -> x.playerUUID == e.uniqueId }

        if (userConfig == null) {
            e.allow()
            return
        }

        if (userConfig.isAllowedToJoin(model.deathTimeout)) {
            e.allow()
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(userConfig.latestDeath, model.deathTimeout)
        val message = TextUtil.getDeathTimerTimeoutMessage(date, userConfig.latestDeath, model.deathTimeout)
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
    }
}
