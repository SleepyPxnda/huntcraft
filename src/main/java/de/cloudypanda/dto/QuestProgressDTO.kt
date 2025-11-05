package de.cloudypanda.dto

import de.cloudypanda.quest.QuestType
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
){
}