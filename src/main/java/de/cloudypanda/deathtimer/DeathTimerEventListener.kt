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

        val deathMessage = PlainTextComponentSerializer.plainText().serialize(e.deathMessage()!!)
        WebhookNotificationManager().sendDeathMessage(deathMessage)
    }

    @EventHandler
    fun onPostRespawnEvent(e: PlayerPostRespawnEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val player =
            transaction {
                PlayerTable.selectAll().where { PlayerTable.uuid eq e.player.uniqueId }.firstOrNull()
            } ?: return

        Huntcraft.instance.logger.info { "Retrieved Player $player" }

        val canJoin = ((System.currentTimeMillis() - player[latestDeathTime]) >= deathTimerConfig.deathTimer)

        if (canJoin) {
            Huntcraft.instance.logger.info { "player is allowed to join" }
            return
        }

        val timeout = DateUtil.getFormattedDurationUntilJoin(0, deathTimerConfig.deathTimer)
        val message = TextUtil.getDeathTimerKickMessage(timeout)
        e.player.kick(message)

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
    }

    @EventHandler
    fun onAsyncPlayerPreLoginEvent(e: AsyncPlayerPreLoginEvent) {
        val deathTimerConfig = huntcraft.configManager.config.death

        val player = transaction {
            PlayerTable.selectAll().where { PlayerTable.uuid eq e.uniqueId }.firstOrNull()
        } ?: return

        Huntcraft.instance.logger.info { "Retrieved Player $player" }

        val canJoin = ((System.currentTimeMillis() - player[latestDeathTime]) >= deathTimerConfig.deathTimer)

        if (canJoin) {
            Huntcraft.instance.logger.info { "player is allowed to join" }
            return
        }

        val date = DateUtil.getFormattedStringForDateAfterMillis(
            player[latestDeathTime],
            deathTimerConfig.deathTimer
        )


        val message = TextUtil.getDeathTimerTimeoutMessage(date, player[latestDeathTime], deathTimerConfig.deathTimer)
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
    }
}
