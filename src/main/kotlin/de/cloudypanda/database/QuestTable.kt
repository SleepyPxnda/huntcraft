package de.cloudypanda.database

import de.cloudypanda.quest.QuestType
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.date
import java.util.*

object QuestTable : UUIDTable("quests") {
    val name = varchar("name", 255)
    val description = text("description")
    val requiredQuests = array<UUID>("required_quest_ids").default(emptyList())
    val afterCompletionText = text("after_completion_text").default("")
    val type = enumeration("type", QuestType::class).default(QuestType.NONE)
    val questProgressionIdentifier = varchar("quest_progression_id", 255)
    val requiredAmount = integer("required_amount").default(0)
    val dateToBeCompleted = date("date_to_be_completed")
}