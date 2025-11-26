package de.cloudypanda.core.event

import de.cloudypanda.Huntcraft
import de.cloudypanda.database.PlayerSessionTable
import de.cloudypanda.database.PlayerTable
import de.cloudypanda.player.PlayerManager
import de.cloudypanda.util.TextUtil
import io.papermc.paper.event.player.AsyncChatEvent
import lombok.extern.slf4j.Slf4j
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.time.Duration.Companion.milliseconds

@Slf4j
class CoreEventListener() : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {

        val player = transaction {
            PlayerTable.selectAll().where { PlayerTable.uuid eq e.player.uniqueId }.firstOrNull()
        }

        if (player == null) {
            //Insert player data into database, ignore errors
            transaction {
                // Ensure the PlayerEntity exists in the database
                PlayerTable.insert {
                    it[uuid] = e.player.uniqueId
                    it[onlineTime] = 0L
                    it[canEnterNether] = false
                    it[canEnterEnd] = false
                }

                PlayerSessionTable.insert {
                    it[playerUuid] = e.player.uniqueId
                    it[loginTime] = System.currentTimeMillis()
                }
            }
        }

        //Load player data into "cache"
        PlayerManager.instance.loadNewPlayer(e.player)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {

        //Retrieve player session from database
        val playerSession = transaction {
            PlayerSessionTable.selectAll().where { PlayerSessionTable.playerUuid eq e.player.uniqueId }.firstOrNull()
        }

        //Retrieve current onlineTime
        val playerOnlineTime = transaction {
            PlayerTable.select(PlayerTable.onlineTime).where { PlayerTable.uuid eq e.player.uniqueId }.firstOrNull()
        }

        if(playerSession == null || playerOnlineTime == null) {
            Huntcraft.instance.logger.warning { "Could not retrieve session or online time for player ${e.player.name}" }
            return
        }

        val previousOnlineTime = playerOnlineTime[PlayerTable.onlineTime]
        val sessionLoginTime = playerSession[PlayerSessionTable.loginTime]

        val sessionDuration = System.currentTimeMillis() - sessionLoginTime

        //Update player online time
        transaction {
            PlayerTable.update({ PlayerTable.uuid eq e.player.uniqueId }) {
                it[onlineTime] = previousOnlineTime + sessionDuration
            }
        }

        //Remove player from "cache"
        PlayerManager.instance.removePlayerByUUID(e.player.uniqueId)

        // Delete player session
        //transaction { PlayerSessionTable.deleteWhere { PlayerSessionTable.playerUuid eq e.player.uniqueId } }

        val sessionDurationString = sessionDuration.milliseconds.toComponents { hours, minutes, seconds, _ ->
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        }

        // Display quit message to player
        e.quitMessage(TextUtil.getQuitIndicator(e.player.name, sessionDurationString))
    }

    @EventHandler
    fun onAsyncChatEvent(e: AsyncChatEvent) {
        e.isCancelled = true
        val player = e.player
        val message = e.message()
        e.message(player.displayName().append(Component.text(": ").append(message)))
        e.viewers().forEach { viewer ->
            viewer.sendMessage(player.displayName().append(Component.text(": ").append(message)))
        }
    }
}