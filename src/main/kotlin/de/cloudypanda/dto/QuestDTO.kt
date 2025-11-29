package de.cloudypanda.dto

import de.cloudypanda.quest.QuestType
import kotlinx.serialization.Serializable

@Serializable
data class QuestDTO (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val afterCompletionText: String = "",
    val type: QuestType = QuestType.NONE,
    val questProgressionIdentifier: String = "",
    val requiredAmount: Int = 0
) {
}