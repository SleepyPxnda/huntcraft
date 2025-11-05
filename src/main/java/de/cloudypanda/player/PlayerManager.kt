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
import java.util.*


class PlayerManager {

    companion object {
        val instance = PlayerManager()
        val playerList = mutableListOf<PlayerDTO>()
    }

    fun getPlayerByUUID(uuid: UUID): PlayerDTO? {
        return playerList.find { it.uuid == uuid }
    }

    fun removePlayerByUUID(uuid: UUID) {
        playerList.removeIf { it.uuid == uuid }
    }

    fun loadNewPlayer(player: Player) {
        // Search for existing player data
        val existingMapPlayer = getPlayerByUUID(player.uniqueId)

        // Clean old data if exists
        if (existingMapPlayer != null) {
            playerList.removeIf { it.uuid == existingMapPlayer.uuid }
        }

        val existingDatabasePlayer =
            transaction { PlayerTable.selectAll().where { PlayerTable.uuid eq player.uniqueId }.firstOrNull() } ?: return

        val ongoingQuestsForPlayer = transaction {
            QuestProgressTable.selectAll()
                .where { QuestProgressTable.playerUuid eq player.uniqueId }
                .map {
                    QuestProgressDTO(
                        playerUuid = it[QuestProgressTable.playerUuid],
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
                otherColumn = QuestTable.id)
                .selectAll()
                .where { (CompletedQuestTable.playerUuid eq player.uniqueId) }
                .map {
                    QuestDTO(
                        id = it[QuestTable.id].toString(),
                        name = it[QuestTable.name],
                        description = it[QuestTable.description],
                        requiredQuests = mutableListOf(), // This would require additional queries to populate // TODO add this, its only informational so it can be ignored for now
                        afterCompletionText = it[QuestTable.afterCompletionText],
                        type = it[QuestTable.type],
                        questProgressionIdentifier = it[QuestTable.questProgressionIdentifier],
                        requiredAmount = it[QuestTable.requiredAmount]
                    )
                }.toMutableList()
        }

        val playerDTO = PlayerDTO(
            uuid = player.uniqueId,
            onlineTime = existingDatabasePlayer?.get(PlayerTable.onlineTime) ?: 0L,
            canEnterNether = existingDatabasePlayer?.get(PlayerTable.canEnterNether) ?: false,
            canEnterEnd = existingDatabasePlayer?.get(PlayerTable.canEnterEnd) ?: false,
            latestDeathTime = existingDatabasePlayer?.get(PlayerTable.latestDeathTime) ?: 0L,
            ongoingQuests = ongoingQuestsForPlayer,
            finishedQuests = completedQuestsForPlayer
        )

        playerList.add(playerDTO)
    }
}