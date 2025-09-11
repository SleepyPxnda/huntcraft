package de.cloudypanda.main.quest

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.ConfigManager
import de.cloudypanda.main.config.QuestConfig
import de.cloudypanda.main.config.QuestListConfig
import de.cloudypanda.main.util.TextUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.io.path.Path


object QuestSystem {

    private var questManager: ConfigManager<QuestConfig> = ConfigManager(
        configPath = Path("huntcraft-quests.json"),
        configClass = QuestConfig::class.java,
        defaultConfig = QuestConfig()
    )
    private var questListManager: ConfigManager<QuestListConfig> = ConfigManager(
        configPath = Path("huntcraft-quests-list.json"),
        configClass = QuestListConfig::class.java,
        defaultConfig = QuestListConfig()
    )

    /**
     * Map to store quests for each player
     */
    var ongoingQuestsMap: MutableMap<UUID, MutableList<QuestDefinition>> = mutableMapOf()
    var completedQuestsMap: MutableMap<UUID, MutableList<QuestDefinition>> = mutableMapOf()
    var questList: MutableList<QuestDefinition> = mutableListOf()

    fun getCurrentQuestsForPlayer(playerId: UUID, questType: QuestType? = null): List<QuestDefinition> {
        if (questType != null) {
            return ongoingQuestsMap[playerId]?.filter { it.type == questType } ?: emptyList()
        }

        return ongoingQuestsMap[playerId] ?: emptyList()
    }

    fun getCompletedQuestsForPlayer(playerId: UUID): List<QuestDefinition> {
        return completedQuestsMap[playerId] ?: emptyList()
    }

    fun executeAchievementEvent(playerId: UUID, achievementId: String) {
        val availableQuestsForType = getCurrentQuestsForPlayer(playerId, QuestType.ACHIEVEMENT)
        for(quest in availableQuestsForType){
            if(quest.achievementIdentifier == achievementId){
                processQuestProgression(playerId, quest.id)
            }
        }
    }

    fun executeItemCraftEvent(playerId: UUID, craftedItem: ItemStack) {
        val availableQuestsForType = getCurrentQuestsForPlayer(playerId, QuestType.ITEM_CRAFT)
        for(quest in availableQuestsForType){
            val parsedConfigItem = ItemStack.of(Material.getMaterial("${quest.itemCraftIdentifier}") ?: Material.AIR)
            if(parsedConfigItem.type == craftedItem.type){
                processQuestProgression(playerId, quest.id, craftedItem.amount)
            }
        }
    }

    fun executeEntityKillEvent(playerId: UUID, killedEntity: Entity) {
        val availableQuestsForType = getCurrentQuestsForPlayer(playerId, QuestType.ENTITY_KILL)
        for(quest in availableQuestsForType){
            val parsedEntity = EntityType.entries.find { it.key.toString() == quest.entityKillIdentifier }
            if(parsedEntity == killedEntity.type){
                processQuestProgression(playerId, quest.id)
            }
        }
    }

    fun executeBlockPlaceEvent(playerId: UUID, placedBlock: Block) {
        val availableQuestsForType = getCurrentQuestsForPlayer(playerId, QuestType.BLOCK_PLACE)
        for(quest in availableQuestsForType){
            val parsedConfigBlock = Material.getMaterial("${quest.blockPlaceIdentifier}")
            if(parsedConfigBlock == placedBlock.type){
                processQuestProgression(playerId, quest.id)
            }
        }
    }

    fun executeBlockBreakEvent(playerId: UUID, brokenBlock: Block) {
        val availableQuestsForType = getCurrentQuestsForPlayer(playerId, QuestType.BLOCK_BREAK)
        for(quest in availableQuestsForType){
            val parsedConfigBlock = Material.getMaterial("${quest.blockBreakIdentifier}")
            if(parsedConfigBlock == brokenBlock.type){
                processQuestProgression(playerId, quest.id)
            }
        }
    }

    fun processQuestProgression(playerId: UUID, questId: String, amount: Int = 1) {
        val playerQuests = ongoingQuestsMap[playerId]?.toMutableList() ?: mutableListOf()
        val questIndex = playerQuests.indexOfFirst { it.id == questId }

        Huntcraft.instance.logger.info { "Processing quest progression for player $playerId, quest $questId, amount $amount" }

        if (questIndex == -1) {
            return
        }

        val quest = playerQuests[questIndex]

        if (quest.completed == true) {
            return
        }

        quest.progression += amount

        Bukkit.getPlayer(playerId)?.sendMessage {
            TextUtil.getQuestProgressBar(quest.name, quest.progression, quest.getNeededCount())
        }

        if (validateQuestCompletion(quest)) {
            completeQuestForPlayer(playerId, quest)
            addNewQuestsForPlayer(playerId)
            Huntcraft.instance.logger.info { "Player $playerId completed quest ${quest.id}" }
            Bukkit.getPlayer(playerId)?.sendMessage { TextUtil.getQuestCompletionMessage(quest.name) }
            Bukkit.getPlayer(playerId)?.sendMessage { TextUtil.getQuestAfterCompletionText(quest.afterCompletionText) }
            Bukkit.broadcast(TextUtil.getQuestCompletionAnnounceMessage(Bukkit.getPlayer(playerId)?.name ?: "Unnamed", quest.name))
            quest.completed = true
        }
        save()
    }

    private fun completeQuestForPlayer(playerId: UUID, quest: QuestDefinition) {
        ongoingQuestsMap[playerId]?.remove(quest)
        val completedQuests = completedQuestsMap[playerId]?.toMutableList() ?: mutableListOf()
        completedQuests.add(quest)
        completedQuestsMap[playerId] = completedQuests
    }

    private fun validateQuestCompletion(quest: QuestDefinition): Boolean {
        return when (quest.type) {
            QuestType.BLOCK_BREAK -> quest.progression >= (quest.requiredBlockBreakCount ?: 0)
            QuestType.BLOCK_PLACE -> quest.progression >= (quest.requiredBlockPlaceCount ?: 0)
            QuestType.ENTITY_KILL -> quest.progression >= (quest.requiredEntityKillCount ?: 0)
            QuestType.ITEM_CRAFT -> quest.progression >= (quest.requiredItemCraftCount ?: 0)
            QuestType.TURN_IN_ITEM -> quest.progression >= (quest.requiredTurnInItemCount ?: 0)
            QuestType.PUZZLE_COMPLETE -> quest.progression >= 1 // Assuming puzzle is binary (completed or not)
            QuestType.ACHIEVEMENT -> quest.progression >= 1 // Assuming achievement is binary (completed or not)
            else -> false
        }
    }

    private fun addNewQuestsForPlayer(playerId: UUID) {
        val playerQuests = ongoingQuestsMap[playerId]?.toMutableList() ?: mutableListOf()
        val completedPlayerQuests = completedQuestsMap[playerId] ?: mutableListOf()

        // Filter 1: Required quests completed
        val (passedRequired, failedRequired) = questList.partition { quest ->
            quest.requiredQuests.all { reqId -> completedPlayerQuests.find { it.id == reqId } != null }
        }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests after requiredQuests filter: ${passedRequired.map { it.name }}" }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by requiredQuests: ${failedRequired.map { it.name }}" }

        // Filter 2: Not already in playerQuests
        val (passedNotOngoing, failedOngoing) = passedRequired.partition { quest ->
            playerQuests.none { it.id == quest.id }
        }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests after not-in-ongoing filter: ${passedNotOngoing.map { it.name }}" }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by already ongoing: ${failedOngoing.map { it.name }}" }

        // Filter 3: Not already completed
        val (passedNotCompleted, failedCompleted) = passedNotOngoing.partition { quest ->
            completedQuestsMap[playerId]?.none { it.id == quest.id } ?: true
        }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests after not-completed filter: ${passedNotCompleted.map { it.name }}" }
//        Huntcraft.instance.logger.info { "[DEBUG] Quests filtered out by already completed: ${failedCompleted.map { it.name }}" }

        val availableQuests = passedNotCompleted

        Huntcraft.instance.logger.info { "Adding ${availableQuests.map { it.name }} for player $playerId" }

        playerQuests.addAll(availableQuests)

        ongoingQuestsMap[playerId] = playerQuests
        save()
    }

    //Load quests from file and fill map
    fun load() {
        questManager.loadConfig()
        questListManager.loadConfig()
        questListManager.config.validateQuests()

        Huntcraft.instance.logger.info { "ConfigManager found ${questManager.config.ongoingQuests.size} players with quests" }
        Huntcraft.instance.logger.info { "ConfigManager found ${questListManager.config.questList} quests" }

        questList = questListManager.config.questList

        ongoingQuestsMap = questManager.config.ongoingQuests
        completedQuestsMap = questManager.config.completedQuests

        Huntcraft.instance.logger.info { "Loading ${questList.size} quests" }

        Bukkit.getOfflinePlayers().forEach { player ->
            addNewQuestsForPlayer(player.uniqueId)
            Huntcraft.instance.logger.info { "Loaded ${player.name} quests from config" }
        }
    }

    fun save() {
        questManager.saveConfig(QuestConfig(ongoingQuestsMap, completedQuestsMap))
    }
}