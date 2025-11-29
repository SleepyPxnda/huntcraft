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

class DeathTimerEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onDeathEvent(e: PlayerDeathEvent) {
        transaction {
            PlayerTable.update({ PlayerTable.uuid eq e.player.uniqueId }) {
                it[latestDeathTime] = System.currentTimeMillis()
            }
        }

        if(huntcraft.configManager.config.discord.enabled) {
            val deathMessage = PlainTextComponentSerializer.plainText().serialize(e.deathMessage()!!)
            WebhookNotificationManager().sendDeathMessage(deathMessage)
        }
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val player =
            transaction {
                PlayerTable.selectAll().where { PlayerTable.uuid eq e.player.uniqueId }.firstOrNull()
            } ?: return


        val date = DateUtil.getFormattedStringForDateAfterMillis(
            player[latestDeathTime],
            deathTimerConfig.deathTimer
        )

        Huntcraft.instance.server.sendMessage(
            TextUtil.getPlayerDeathAnnounceMessage(
                e.player.name,
                date
            )
        )
        println(TextUtil.getPlayerDeathAnnounceMessage(e.player.name, date))

        e.player.kick(
            TextUtil.getDeathTimerTimeoutMessage(
                date,
                player[latestDeathTime],
                deathTimerConfig.deathTimer
            )
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        Huntcraft.instance.logger.info { "🔍 AsyncPlayerPreLoginEvent triggered for player: ${e.name} (UUID: ${e.uniqueId})" }

        val player = transaction {
            PlayerTable.selectAll().where { PlayerTable.uuid eq e.uniqueId }.firstOrNull()
        }

        if (player == null) {
            Huntcraft.instance.logger.info { "✅ Player ${e.name} not found in database - allowing join (new player)" }
            return
        }

        Huntcraft.instance.logger.info { "📊 Player ${e.name} found in database" }

        val currentTime = System.currentTimeMillis()
        val lastDeathTime = player[latestDeathTime]
        val timeSinceDeath = currentTime - lastDeathTime
        val deathTimeoutMs = deathTimerConfig.deathTimer

        Huntcraft.instance.logger.info { "⏰ Current time: $currentTime" }
        Huntcraft.instance.logger.info { "💀 Last death time: $lastDeathTime" }
        Huntcraft.instance.logger.info { "⏳ Time since death: $timeSinceDeath ms" }
        Huntcraft.instance.logger.info { "🔒 Death timeout config: $deathTimeoutMs ms" }

        val canJoin = timeSinceDeath >= deathTimeoutMs

        if (canJoin) {
            Huntcraft.instance.logger.info { "✅ Player ${e.name} is allowed to join - timeout expired" }
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(lastDeathTime, deathTimeoutMs)

        Huntcraft.instance.logger.warning { "🚫 Player ${e.name} blocked from joining until $date" }
        Huntcraft.instance.logger.info { "📛 Remaining timeout: ${deathTimeoutMs - timeSinceDeath} ms" }

        val messageComponent = TextUtil.getDeathTimerTimeoutMessage(date, lastDeathTime, deathTimeoutMs)
        //e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, messageComponent)

        Huntcraft.instance.logger.info { "🔔 Kick message sent to player ${e.name}" }
    }
}
