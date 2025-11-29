package de.cloudypanda.database

import org.jetbrains.exposed.dao.id.UUIDTable

object CompletedQuestTable : UUIDTable("completed_quests") {
    val playerUuid = uuid("player_uuid").references(PlayerTable.uuid)
    val questId = uuid("quest_uuid").references(QuestTable.id)
}