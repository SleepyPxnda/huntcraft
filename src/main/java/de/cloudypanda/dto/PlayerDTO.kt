package de.cloudypanda.dto

import de.cloudypanda.Huntcraft
import de.cloudypanda.util.TextUtil
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import java.util.*

@Serializable
data class PlayerDTO(
    @Contextual
    val uuid: UUID,
    var onlineTime: Long,
    val latestDeathTime: Long,
    var canEnterNether: Boolean,
    var canEnterEnd: Boolean,
    var ongoingQuests: MutableList<QuestProgressDTO> = mutableListOf(),
    var finishedQuests: MutableList<QuestDTO> = mutableListOf(),
) {

    fun executeAchievementEvent(playerId: UUID, achievementId: String) {
        for(quest in ongoingQuests){
            if(quest.progressingIdentifier == achievementId){
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeItemCraftEvent(playerId: UUID, craftedItem: ItemStack) {
        for(quest in ongoingQuests){
            val parsedConfigItem = ItemStack.of(Material.getMaterial(quest.progressingIdentifier) ?: Material.AIR)
            if(parsedConfigItem.type == craftedItem.type){
                processQuestProgression(playerId, quest.questId, craftedItem.amount)
            }
        }
    }

    fun executeEntityKillEvent(playerId: UUID, killedEntity: Entity) {
        for(quest in ongoingQuests){
            val parsedEntity = EntityType.entries.find { it.key.toString() == quest.progressingIdentifier }
            if(parsedEntity == killedEntity.type){
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeBlockPlaceEvent(playerId: UUID, placedBlock: Block) {
        for(quest in ongoingQuests){
            val parsedConfigBlock = Material.getMaterial(quest.progressingIdentifier)
            if(parsedConfigBlock == placedBlock.type){
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeBlockBreakEvent(playerId: UUID, brokenBlock: Block) {
        for(quest in ongoingQuests){
            val parsedConfigBlock = Material.getMaterial(quest.progressingIdentifier)
            if(parsedConfigBlock == brokenBlock.type){
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun processQuestProgression(playerId: UUID, questId: UUID, amount: Int = 1) {
        val questIndex = ongoingQuests.indexOfFirst { it.questId == questId }

        Huntcraft.instance.logger.info { "Processing quest progression for player $playerId, quest $questId, amount $amount" }

        if (questIndex == -1) {
            return
        }

        val quest = ongoingQuests[questIndex]

        quest.progression += amount

        Bukkit.getPlayer(playerId)?.sendMessage {
            TextUtil.getQuestProgressBar(quest.name, quest.progression, quest.requiredAmount)
        }

        //if (validateQuestCompletion(quest)) {
        //    completeQuestForPlayer(questId)
        //    addNewQuestsForPlayer(playerId)
        //    Huntcraft.instance.logger.info { "Player $playerId completed quest ${quest.id}" }
        //    Bukkit.getPlayer(playerId)?.sendMessage { TextUtil.Companion.getQuestCompletionMessage(quest.name) }
        //    Bukkit.getPlayer(playerId)?.sendMessage { TextUtil.Companion.getQuestAfterCompletionText(quest.afterCompletionText) }
        //    Bukkit.broadcast(TextUtil.Companion.getQuestCompletionAnnounceMessage(Bukkit.getPlayer(playerId)?.name ?: "Unnamed", quest.name))
        //    quest.completed = true
        //}
    }

//    private fun validateQuestCompletion(quest: QuestDefinition): Boolean {
//        return when (quest.type) {
//            QuestType.BLOCK_BREAK -> quest.progression >= (quest.requiredBlockBreakCount ?: 0)
//            QuestType.BLOCK_PLACE -> quest.progression >= (quest.requiredBlockPlaceCount ?: 0)
//            QuestType.ENTITY_KILL -> quest.progression >= (quest.requiredEntityKillCount ?: 0)
//            QuestType.ITEM_CRAFT -> quest.progression >= (quest.requiredItemCraftCount ?: 0)
//            QuestType.TURN_IN_ITEM -> quest.progression >= (quest.requiredTurnInItemCount ?: 0)
//            QuestType.PUZZLE_COMPLETE -> quest.progression >= 1 // Assuming puzzle is binary (completed or not)
//            QuestType.ACHIEVEMENT -> quest.progression >= 1 // Assuming achievement is binary (completed or not)
//            else -> false
//        }
//    }

//    private fun completeQuestForPlayer(questId: UUID) {
//        ongoingQuests.removeIf { it.questId == questId }

//        val completedQuests = finishedQuests.toMutableList()
//        completedQuests.add(quest)
//    }

//    private fun addNewQuestsForPlayer(playerId: UUID, questList: List<QuestDefinition>) {

//        // Filter 1: Required quests completed
//        val (passedRequired, failedRequired) = questList.partition { quest ->
//            quest.requiredQuests.all { reqId -> finishedQuests.find { it.id == reqId } != null }
//        }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests after requiredQuests filter: ${passedRequired.map { it.name }}" }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by requiredQuests: ${failedRequired.map { it.name }}" }

//        // Filter 2: Not already in playerQuests
//        val (passedNotOngoing, failedOngoing) = passedRequired.partition { quest ->
//            playerQuests.none { it.id == quest.id }
//        }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests after not-in-ongoing filter: ${passedNotOngoing.map { it.name }}" }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by already ongoing: ${failedOngoing.map { it.name }}" }

//        // Filter 3: Not already completed
//        val (passedNotCompleted, failedCompleted) = passedNotOngoing.partition { quest ->
//            completedQuestsMap[playerId]?.none { it.id == quest.id } ?: true
//        }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests after not-completed filter: ${passedNotCompleted.map { it.name }}" }
////        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by already completed: ${failedCompleted.map { it.name }}" }

//        val availableQuests = passedNotCompleted

//        Huntcraft.Companion.instance.logger.info { "Adding ${availableQuests.map { it.name }} for player $playerId" }

//        playerQuests.addAll(availableQuests)

//        ongoingQuestsMap[playerId] = playerQuests
//        save()
//    }
}