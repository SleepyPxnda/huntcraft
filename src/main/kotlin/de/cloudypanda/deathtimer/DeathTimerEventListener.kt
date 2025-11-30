package de.cloudypanda.deathtimer

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import de.cloudypanda.Huntcraft
import de.cloudypanda.core.integrations.discord.WebhookNotificationManager
import de.cloudypanda.database.PlayerTable
import de.cloudypanda.database.PlayerTable.latestDeathTime
import de.cloudypanda.util.DateUtil
import de.cloudypanda.util.TextUtil
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DeathTimerEventListener() : Listener {

    val deathTimerTimeout = Huntcraft.instance.config.getLong("deathTimer.timeoutInSeconds")

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {

        var deathTime = System.currentTimeMillis();

        transaction {
            PlayerTable.update({ PlayerTable.uuid eq e.player.uniqueId }) {
                it[latestDeathTime] = deathTime
            }
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(
            deathTime,
            deathTimerTimeout
        )

        val playerDeathMessage = TextUtil.getPlayerDeathAnnounceMessage(
            e.player.name,
            date
        )

        Huntcraft.instance.server.sendMessage(playerDeathMessage)

        println("Sending death message")
        val deathMessage = PlainTextComponentSerializer.plainText().serialize(playerDeathMessage)
        println(deathMessage)
        WebhookNotificationManager().sendDeathMessage(deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val player =
            transaction {
                PlayerTable.selectAll().where { PlayerTable.uuid eq e.player.uniqueId }.firstOrNull()
            } ?: return

        val date = DateUtil.getFormattedStringForDateAfterMillis(
            player[latestDeathTime],
            deathTimerTimeout
        )

        e.player.kick(TextUtil.getDeathTimerKickMessage(date))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val player = transaction {
            PlayerTable.selectAll().where { PlayerTable.uuid eq e.uniqueId }.firstOrNull()
        }

        if (player == null) {
            return
        }

        val currentTime = System.currentTimeMillis()
        val lastDeathTime = player[latestDeathTime]
        val timeSinceDeath = currentTime - lastDeathTime
        val deathTimeoutMs = deathTimerTimeout * 1000

        val canJoin = timeSinceDeath >= deathTimeoutMs

        if (canJoin) {
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(lastDeathTime, deathTimeoutMs)

        val messageComponent = TextUtil.getDeathTimerTimeoutMessage(date, lastDeathTime, deathTimeoutMs)
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, messageComponent)
    }
}
