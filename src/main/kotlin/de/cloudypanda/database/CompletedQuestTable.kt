package de.cloudypanda.database

import de.cloudypanda.quest.QuestCompletionState
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object CompletedQuestTable : UUIDTable("completed_quests") {
    val playerUuid = uuid("player_uuid").references(PlayerTable.uuid)
    val questId = uuid("quest_uuid").references(QuestTable.id)
    val completionState = enumeration("completion_state", QuestCompletionState::class).default(QuestCompletionState.NONE)
    val completedOn = date("completed_on")
}