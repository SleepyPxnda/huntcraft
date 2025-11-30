package de.cloudypanda.dto

import de.cloudypanda.quest.QuestType
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class QuestProgressDTO(
    @Contextual
    val playerUuid: UUID = UUID.randomUUID(),
    @Contextual
    val questId: UUID = UUID.randomUUID(),
    val name: String = "",
    var progression: Int = 0,
    val requiredAmount: Int = 0,
    val type: QuestType = QuestType.NONE,
    val progressingIdentifier: String = "",
    val dateToBeCompleted: LocalDate = LocalDate(1970,1,1)
){
}