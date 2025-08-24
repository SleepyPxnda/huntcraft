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
    val blockBreakTypeIdentifier: String? = null,

    // For BLOCK_PLACE
    val requiredBlockPlaceCount: Int? = 0,
    val blockPlaceType: String? = null,

    // For ENTITY_KILL
    val requiredEntityKillCount: Int? = 0,
    val entityKillType: String? = null,

    // For ITEM_CRAFT
    val requiredItemCraftCount: Int? = 0,
    val itemCraftType: Material? = null,

    // For TURN_IN_ITEM
    val requiredTurnInItemCount: Int? = 0,
    val turnInItemType: String? = null,

    // For ACHIEVEMENT
    val achievementType: String? = null,
)