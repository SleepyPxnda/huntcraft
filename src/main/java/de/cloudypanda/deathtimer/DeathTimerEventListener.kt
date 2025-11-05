package de.cloudypanda.deathtimer

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.Huntcraft
import de.cloudypanda.core.integrations.discord.WebhookNotificationManager
import de.cloudypanda.database.PlayerTable
import de.cloudypanda.database.PlayerTable.isAllowedToJoin
import de.cloudypanda.util.DateUtil
import de.cloudypanda.util.TextUtil
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.updateReturning

class DeathTimerEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {
        transaction {
            PlayerTable.updateReturning(PlayerTable.columns, { PlayerTable.uuid eq e.player.uniqueId }) {
                it[latestDeathTime] = System.currentTimeMillis()
            }
        }

        val deathMessage = PlainTextComponentSerializer.plainText().serialize(e.deathMessage()!!)
        WebhookNotificationManager().sendDeathMessage(deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val player = PlayerTable.selectAll().where { PlayerTable.uuid eq e.player.uniqueId }.first()

        if (player.isAllowedToJoin(deathTimerConfig.deathTimer)) {
            return
        }

        val timeout = DateUtil.getFormattedDurationUntilJoin(0, deathTimerConfig.deathTimer)
        val message = TextUtil.getDeathTimerKickMessage(timeout)
        e.player.kick(message)

        val date = DateUtil.getFormattedStringForDateAfterMillis(
            player[PlayerTable.latestDeathTime],
            deathTimerConfig.deathTimer
        )
        Huntcraft.instance.server.sendMessage(
            TextUtil.getPlayerDeathAnnounceMessage(
                e.player.name,
                date
            )
        )
        println(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val player = transaction {
            PlayerTable.selectAll().where { PlayerTable.uuid eq e.uniqueId }.firstOrNull()
        } ?: return

        if (player.isAllowedToJoin(deathTimerConfig.deathTimer)) {
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(player[PlayerTable.latestDeathTime], deathTimerConfig.deathTimer)
        val message = TextUtil.getDeathTimerTimeoutMessage(date, player[PlayerTable.latestDeathTime], deathTimerConfig.deathTimer)
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
    }
}
