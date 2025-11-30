package de.cloudypanda.player

import de.cloudypanda.database.CompletedQuestTable
import de.cloudypanda.database.PlayerTable
import de.cloudypanda.database.QuestProgressTable
import de.cloudypanda.database.QuestTable
import de.cloudypanda.dto.PlayerDTO
import de.cloudypanda.dto.QuestDTO
import de.cloudypanda.dto.QuestProgressDTO
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlayerManager {
    private val log = LoggerFactory.getLogger(PlayerManager::class.java)
    private val players = ConcurrentHashMap<UUID, PlayerDTO>()

    fun getPlayerByUUID(uuid: UUID): PlayerDTO? = players[uuid]

    fun removePlayerByUUID(uuid: UUID) {
        players.remove(uuid)
        log.debug("Removed player from cache: {}", uuid)
    }

    fun loadNewPlayer(player: Player) {
        val uuid = player.uniqueId
        // replace existing cached entry if present
        players.remove(uuid)

        try {
            val existingDatabasePlayer = transaction {
                PlayerTable.selectAll().where { PlayerTable.uuid eq uuid }.firstOrNull()
            }

            if (existingDatabasePlayer == null) {
                log.warn("No database entry for player $uuid; skipping loadNewPlayer.")
                return
            }

            val ongoingQuestsForPlayer = transaction {
                QuestProgressTable.selectAll()
                    .where { QuestProgressTable.playerUuid eq uuid }
                    .map {
                        QuestProgressDTO(
                            playerUuid = it[QuestProgressTable.playerUuid],
                            name = it[QuestProgressTable.name],
                            questId = it[QuestProgressTable.questId],
                            progression = it[QuestProgressTable.progression],
                            requiredAmount = it[QuestProgressTable.requiredAmount],
                            type = it[QuestProgressTable.type],
                            progressingIdentifier = it[QuestProgressTable.progressingIdentifier],
                        )
                    }.toMutableList()
            }

            val completedQuestsForPlayer = transaction {
                CompletedQuestTable.join(
                    QuestTable,
                    JoinType.INNER,
                    onColumn = CompletedQuestTable.questId,
                    otherColumn = QuestTable.id
                )
                    .selectAll()
                    .where { CompletedQuestTable.playerUuid eq uuid }
                    .map {
                        QuestDTO(
                            id = it[QuestTable.id].toString(),
                            name = it[QuestTable.name],
                            description = it[QuestTable.description],
                            afterCompletionText = it[QuestTable.afterCompletionText],
                            type = it[QuestTable.type],
                            questProgressionIdentifier = it[QuestTable.questProgressionIdentifier],
                            requiredAmount = it[QuestTable.requiredAmount]
                        )
                    }.toMutableList()
            }

            val playerDTO = PlayerDTO(
                uuid = uuid,
                onlineTime = existingDatabasePlayer[PlayerTable.onlineTime],
                canEnterNether = existingDatabasePlayer[PlayerTable.canEnterNether],
                canEnterEnd = existingDatabasePlayer[PlayerTable.canEnterEnd],
                latestDeathTime = existingDatabasePlayer[PlayerTable.latestDeathTime],
                ongoingQuests = ongoingQuestsForPlayer,
                finishedQuests = completedQuestsForPlayer
            )

            players[uuid] = playerDTO
            log.debug("Loaded player into cache: {}", uuid)
        } catch (ex: Exception) {
            log.error("Failed to load player $uuid", ex)
        }
    }
}