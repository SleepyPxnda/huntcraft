package de.cloudypanda.dto

import de.cloudypanda.quest.QuestType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class QuestDTO (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val requiredQuests: MutableList<@Contextual UUID> = mutableListOf(),
    val afterCompletionText: String = "",
    val type: QuestType = QuestType.NONE,
    val questProgressionIdentifier: String = "",
    val requiredAmount: Int = 0
) {
}