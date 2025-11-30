package de.cloudypanda.core.event

import de.cloudypanda.Huntcraft
import de.cloudypanda.database.*
import de.cloudypanda.player.PlayerManager
import de.cloudypanda.quest.QuestCompletionState
import de.cloudypanda.util.DateUtil
import de.cloudypanda.util.TextUtil
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.datetime.LocalDate
import lombok.extern.slf4j.Slf4j
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Slf4j
class CoreEventListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {

        // Ensure there is a player record and a session in the database
        ensurePlayerRecordAndSession(e.player.uniqueId)

        // Assign available quests and load the player into the in-memory cache
        assignAvailableQuestsToPlayer(e.player.uniqueId)

        // Cancel failed quests
        cancelFailedQuestsForPlayer(e.player.uniqueId)

        PlayerManager.loadNewPlayer(e.player)

        Huntcraft.instance.server.sendMessage { TextUtil.getJoinIndicator(e.player.name) }

        val deathTimeout = Huntcraft.instance.config.getInt("deathTimer.timeoutInSeconds")
        e.player.sendMessage { TextUtil.getJoinMessage(deathTimeout) }
    }

    private fun cancelFailedQuestsForPlayer(uniqueId: UUID) {
        val failedQuests = transaction {
            QuestProgressTable.selectAll().filter {
                it[QuestProgressTable.playerUuid] == uniqueId && it[QuestProgressTable.dateToBeCompleted] < DateUtil.currentLocalDate()
            }.toList()
        }

        if (failedQuests.isEmpty()) return

        transaction {
            failedQuests.forEach { questProgress ->
                // Move to CompletedQuestTable with CANCELLED state
                CompletedQuestTable.insert {
                    it[CompletedQuestTable.playerUuid] = uniqueId
                    it[CompletedQuestTable.questId] = questProgress[QuestProgressTable.questId]
                    it[completedOn] = DateUtil.currentLocalDate()
                    it[CompletedQuestTable.completionState] = QuestCompletionState.CANCELLED
                }

                // Remove from QuestProgressTable
                QuestProgressTable.deleteWhere { QuestProgressTable.id eq questProgress[QuestProgressTable.id] }

                Huntcraft.instance.logger.info {
                    "Cancelled failed quest '${questProgress[QuestProgressTable.questId]}' for player $uniqueId"
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerQuitEvent(e: PlayerQuitEvent) {

        //Retrieve player session from database
        val playerSession = transaction {
            PlayerSessionTable.selectAll().firstOrNull { it[PlayerSessionTable.playerUuid] == e.player.uniqueId }
        }

        //Retrieve current onlineTime
        val playerOnlineTime = transaction {
            PlayerTable.selectAll().firstOrNull { it[PlayerTable.uuid] == e.player.uniqueId }
        }

        if (playerSession == null || playerOnlineTime == null) {
            Huntcraft.instance.logger.warning { "Could not retrieve session or online time for player ${e.player.name}" }
            return
        }

        val previousOnlineTime = playerOnlineTime[PlayerTable.onlineTime]
        val sessionLoginTime = playerSession[PlayerSessionTable.loginTime]

        val sessionDuration = System.currentTimeMillis() - sessionLoginTime

        //Update player online time
        transaction {
            PlayerTable.update({ PlayerTable.uuid eq e.player.uniqueId }) {
                it[PlayerTable.onlineTime] = previousOnlineTime + sessionDuration
            }
        }

        //Remove player from "cache"
        PlayerManager.removePlayerByUUID(e.player.uniqueId)

        // Delete player session
//        transaction {
//            PlayerSessionTable.deleteWhere { PlayerSessionTable.playerUuid eq e.player.uniqueId }
//        }

        val sessionDurationString = sessionDuration.milliseconds.toComponents { hours, minutes, seconds, _ ->
            "%02d:%02d:%02d".format(
                hours,
                minutes,
                seconds
            )
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

    // Helper: Ensure player record exists and create a session if needed
    private fun ensurePlayerRecordAndSession(uuid: UUID) {
        transaction {
            val existing = PlayerTable.selectAll().firstOrNull { it[PlayerTable.uuid] == uuid }
            if (existing == null) {
                PlayerTable.insert {
                    it[PlayerTable.uuid] = uuid
                    it[PlayerTable.onlineTime] = 0L
                }

                PlayerSessionTable.insert {
                    it[PlayerSessionTable.playerUuid] = uuid
                    it[PlayerSessionTable.loginTime] = System.currentTimeMillis()
                }
            } else {
                // Even if the player exists, ensure a session record exists for this login
                val session = PlayerSessionTable.selectAll().firstOrNull { it[PlayerSessionTable.playerUuid] == uuid }
                if (session == null) {
                    PlayerSessionTable.insert {
                        it[PlayerSessionTable.playerUuid] = uuid
                        it[PlayerSessionTable.loginTime] = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    // Helper: Assign all possible quests to the player (not already completed/ongoing and meeting requirements)
    private fun assignAvailableQuestsToPlayer(playerUuid: UUID) {
        val completedQuestIds = fetchCompletedQuestIds(playerUuid)
        val ongoingQuestIds = fetchOngoingQuestIds(playerUuid)
        val unavailableQuestIds = completedQuestIds + ongoingQuestIds

        val allPossibleQuests = fetchQuestsNotInListAndDueOn(unavailableQuestIds, DateUtil.currentLocalDate())

        val possibleQuests = allPossibleQuests.filter { quest ->
            val requiredQuestIds = quest[QuestTable.requiredQuests]
            requiredQuestIds.isEmpty() || requiredQuestIds.all { it in completedQuestIds }
        }

        if (possibleQuests.isEmpty()) return

        transaction {
            possibleQuests.forEach { quest ->
                QuestProgressTable.insert {
                    it[QuestProgressTable.playerUuid] = playerUuid
                    it[QuestProgressTable.name] = quest[QuestTable.name]
                    // quest id value may be Int or UUID depending on table definition; use .value to extract
                    it[QuestProgressTable.questId] = quest[QuestTable.id].value
                    it[QuestProgressTable.progression] = 0
                    it[QuestProgressTable.requiredAmount] = quest[QuestTable.requiredAmount]
                    it[QuestProgressTable.type] = quest[QuestTable.type]
                    it[QuestProgressTable.progressingIdentifier] = quest[QuestTable.questProgressionIdentifier]
                    it[QuestProgressTable.dateToBeCompleted] = quest[QuestTable.dateToBeCompleted]
                }

                Huntcraft.instance.logger.info {
                    "Assigned new quest '${quest[QuestTable.id]}' to player $playerUuid"
                }
            }
        }
    }

    private fun fetchCompletedQuestIds(playerUuid: UUID): List<UUID> = transaction {
        CompletedQuestTable.selectAll().filter { it[CompletedQuestTable.playerUuid] == playerUuid }
            .map { it[CompletedQuestTable.questId] }
    }

    private fun fetchOngoingQuestIds(playerUuid: UUID): List<UUID> = transaction {
        QuestProgressTable.selectAll().filter { it[QuestProgressTable.playerUuid] == playerUuid }
            .map { it[QuestProgressTable.questId] }
    }

    private fun fetchQuestsNotInListAndDueOn(unavailableIds: List<UUID>, date: LocalDate): List<ResultRow> = transaction {
        // fetch all quests for the date and filter in-memory to avoid SQL type mismatches
        QuestTable.selectAll().filter { it[QuestTable.dateToBeCompleted] == date }
            .filter { quest ->
                val questIdValue = quest[QuestTable.id].value
                // Compare quest id value with unavailable ids (UUID or Int depending on schema)
                !unavailableIds.contains(questIdValue)
            }
            .toList()
    }
}