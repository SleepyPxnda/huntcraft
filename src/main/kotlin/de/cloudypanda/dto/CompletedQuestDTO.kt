package de.cloudypanda.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CompletedQuestDTO (
    @Contextual
    val playerUuid: UUID,
    @Contextual
    val questId: UUID,
) {
}