package de.cloudypanda.dto

import de.cloudypanda.quest.QuestCompletionState
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable


@Serializable
data class CompletedQuestDTO (
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val completionState: QuestCompletionState = QuestCompletionState.NONE,
    val completedOn: LocalDate = LocalDate(1970,1,1)
)