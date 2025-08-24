package de.cloudypanda.main.config

import de.cloudypanda.main.quest.QuestDefinition
import de.cloudypanda.main.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class QuestConfig(
    val ongoingQuests: MutableMap<@Serializable(with = UUIDSerializer::class) UUID, MutableList<QuestDefinition>> = mutableMapOf(),
    val completedQuests: MutableMap<@Serializable(with = UUIDSerializer::class) UUID, MutableList<QuestDefinition>> = mutableMapOf()
) {

}