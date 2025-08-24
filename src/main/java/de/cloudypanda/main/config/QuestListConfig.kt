package de.cloudypanda.main.config

import de.cloudypanda.main.quest.QuestDefinition
import kotlinx.serialization.Serializable

@Serializable
data class QuestListConfig(
    val questList: MutableList<QuestDefinition> = mutableListOf(),
) {
    fun validateQuests() {
        for (quest in questList) {
            val missingFields = mutableListOf<String>()
            if (quest.id.isBlank()) missingFields.add("id")
            if (quest.name.isBlank()) missingFields.add("name")
            if (quest.description.isBlank()) missingFields.add("description")
            if (quest.type.name == "NONE") missingFields.add("type")
            if (missingFields.isNotEmpty()) {
                println(
                    "[QuestConfig] Warning: Quest with id='${quest.id}' is missing fields: ${
                        missingFields.joinToString(
                            ", "
                        )
                    }"
                )
            }
        }
    }
}
