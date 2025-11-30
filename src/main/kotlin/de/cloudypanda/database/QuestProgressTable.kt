package de.cloudypanda.database

import de.cloudypanda.quest.QuestType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object QuestProgressTable : UUIDTable("quest_progress") {
    val playerUuid = uuid("player_uuid").references(PlayerTable.uuid)
    val questId = uuid("quest_id").references(QuestTable.id)
    val name = varchar("name", 255)
    val progression = integer("progression").default(0)
    val requiredAmount = integer("required_amount").default(0)
    val type = enumeration("type", QuestType::class).default(QuestType.NONE)
    val progressingIdentifier = varchar("progressing_identifier", length = 255).default("")
    val dateToBeCompleted = date("date_to_be_completed")
}