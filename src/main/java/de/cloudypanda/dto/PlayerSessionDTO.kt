package de.cloudypanda.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PlayerSessionDTO(
    @Contextual
    val playerUUID: UUID = UUID.randomUUID(),
    val loginTime: Long = 0L,
) {
}