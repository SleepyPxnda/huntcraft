package de.cloudypanda.main.core.integrations.rest.models

import java.util.*

data class UpdatePlayerDeathDto(
    val uuid: UUID,
    val deathLocation_X: Double,
    val deathLocation_Y: Double,
    val deathLocation_Z: Double,
    val deathLocation_World: String,
    val deathTimestamp: Long,
)
