package de.cloudypanda.main.core.integrations.rest.models

import java.util.*

data class UpdatePlayerDeathDto(
    val uuid: UUID,
    val deathTimestamp: Long,
)
