package de.cloudypanda.dto

import de.cloudypanda.Huntcraft
import de.cloudypanda.database.CompletedQuestTable
import de.cloudypanda.database.QuestProgressTable
import de.cloudypanda.database.QuestTable
import de.cloudypanda.quest.QuestType
import de.cloudypanda.util.TextUtil
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
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
        for (quest in ongoingQuests) {
            if (quest.progressingIdentifier == achievementId) {
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeItemCraftEvent(playerId: UUID, craftedItem: ItemStack) {
        for (quest in ongoingQuests.filter { quest -> QuestType.ITEM_CRAFT == quest.type }) {
            val parsedConfigItem = ItemStack.of(Material.getMaterial(quest.progressingIdentifier) ?: Material.AIR)
            if (parsedConfigItem.type == craftedItem.type) {
                processQuestProgression(playerId, quest.questId, craftedItem.amount)
            }
        }
    }

    fun executeEntityKillEvent(playerId: UUID, killedEntity: Entity) {
        for (quest in ongoingQuests.filter { quest -> QuestType.ENTITY_KILL == quest.type }) {
            val parsedEntity = EntityType.entries.find { it.key.toString() == quest.progressingIdentifier }
            if (parsedEntity == killedEntity.type) {
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeBlockPlaceEvent(playerId: UUID, placedBlock: Block) {
        for (quest in ongoingQuests.filter { quest -> QuestType.BLOCK_PLACE == quest.type }) {
            val parsedConfigBlock = Material.getMaterial(quest.progressingIdentifier)
            if (parsedConfigBlock == placedBlock.type) {
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun executeBlockBreakEvent(playerId: UUID, brokenBlock: Block) {
        for (quest in ongoingQuests.filter { quest -> QuestType.BLOCK_BREAK == quest.type }) {
            val parsedConfigBlock = Material.getMaterial(quest.progressingIdentifier)
            if (parsedConfigBlock == brokenBlock.type) {
                processQuestProgression(playerId, quest.questId)
            }
        }
    }

    fun processQuestProgression(playerUUID: UUID, questUUID: UUID, amount: Int = 1) {

        if(ongoingQuests.isEmpty()) return

        val questIndex = ongoingQuests.indexOfFirst { it.questId == questUUID }

        Huntcraft.instance.logger.info { "Processing quest progression for player $playerUUID, quest $questUUID, amount $amount" }

        if (questIndex == -1) return

        val quest = ongoingQuests[questIndex]

        quest.progression += amount

        Bukkit.getPlayer(playerUUID)?.sendMessage {
            TextUtil.getQuestProgressBar(quest.name, quest.progression, quest.requiredAmount)
        }

        if (quest.progression < quest.requiredAmount) return

        val finishedQuest =
            transaction { QuestTable.selectAll().where { QuestTable.id eq quest.questId }.firstOrNull() }

        if (finishedQuest == null) {
            Huntcraft.instance.logger.warning { "Could not find quest definition for questId $questUUID" }
            return
        }

        //Broadcast completion message
        Bukkit.getPlayer(playerUUID)
            ?.sendMessage { TextUtil.getQuestCompletionMessage(finishedQuest[QuestTable.name]) }
        Bukkit.getPlayer(playerUUID)
            ?.sendMessage { TextUtil.getQuestAfterCompletionText(finishedQuest[QuestTable.afterCompletionText]) }

        //Remove Quest from cache
        ongoingQuests.removeAt(questIndex)

        //Update Database
        transaction {
            QuestProgressTable.deleteWhere { QuestProgressTable.playerUuid eq playerUUID and (QuestProgressTable.questId eq questUUID) }
            CompletedQuestTable.insert {
                it[playerUuid] = playerUUID
                it[questId] = questUUID
            }
        }

        //Add to finished quests in cache
        finishedQuests.add(
            QuestDTO(
                id = finishedQuest[QuestTable.id].toString(),
                name = finishedQuest[QuestTable.name],
                description = finishedQuest[QuestTable.description],
                afterCompletionText = finishedQuest[QuestTable.afterCompletionText],
                type = finishedQuest[QuestTable.type],
                questProgressionIdentifier = finishedQuest[QuestTable.questProgressionIdentifier],
                requiredAmount = finishedQuest[QuestTable.requiredAmount]
            )
        )
    }

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