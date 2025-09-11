package de.cloudypanda.main.quest

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class QuestDefinition (
    val id: String = "",
    val name: String= "",
    val description: String= "",
    val afterCompletionText: String= "",
    val type: QuestType = QuestType.NONE,
    val requiredQuests: List<String> = listOf(),
    var progression: Int = 0,  // Current progression of the quest
    var completed: Boolean? = false,

    //Types are only filled when questType is correct

    // For BLOCK_BREAK
    val requiredBlockBreakCount: Int? = 0,
    val blockBreakIdentifier: String? = null,

    // For BLOCK_PLACE
    val requiredBlockPlaceCount: Int? = 0,
    val blockPlaceIdentifier: String? = null,

    // For ENTITY_KILL
    val requiredEntityKillCount: Int? = 0,
    val entityKillIdentifier: String? = null,

    // For ITEM_CRAFT
    val requiredItemCraftCount: Int? = 0,
    val itemCraftIdentifier: Material? = null,

    // For TURN_IN_ITEM
    val requiredTurnInItemCount: Int? = 0,
    val turnInItemIdentifier: String? = null,

    // For ACHIEVEMENT
    val achievementIdentifier: String? = null,
) {
    fun isCompleted(): Boolean {
        return when (type) {
            QuestType.BLOCK_BREAK -> progression >= (requiredBlockBreakCount ?: 0)
            QuestType.BLOCK_PLACE -> progression >= (requiredBlockPlaceCount ?: 0)
            QuestType.ENTITY_KILL -> progression >= (requiredEntityKillCount ?: 0)
            QuestType.ITEM_CRAFT -> progression >= (requiredItemCraftCount ?: 0)
            QuestType.TURN_IN_ITEM -> progression >= (requiredTurnInItemCount ?: 0)
            QuestType.PUZZLE_COMPLETE -> progression >= 1
            QuestType.ACHIEVEMENT -> progression >= 1
            else -> false
        }
    }

    fun getNeededCount() : Int {
        return when (type) {
            QuestType.BLOCK_BREAK -> requiredBlockBreakCount ?: 0
            QuestType.BLOCK_PLACE -> requiredBlockPlaceCount ?: 0
            QuestType.ENTITY_KILL -> requiredEntityKillCount ?: 0
            QuestType.ITEM_CRAFT -> requiredItemCraftCount ?: 0
            QuestType.TURN_IN_ITEM -> requiredTurnInItemCount ?: 0
            QuestType.PUZZLE_COMPLETE -> 1
            QuestType.ACHIEVEMENT -> 1
            else -> 0
        }
    }
}