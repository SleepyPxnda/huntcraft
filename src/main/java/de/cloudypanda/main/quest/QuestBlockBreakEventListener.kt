package de.cloudypanda.main.quest

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class QuestBlockBreakEventListener () : Listener {

    @EventHandler()
    fun onBlockBreak(event: BlockBreakEvent){
        val availableQuestsForType = QuestSystem.getCurrentQuestsForPlayer(event.player.uniqueId, QuestType.BLOCK_BREAK)
        for(quest in availableQuestsForType){
            val parsedConfigBlock = Material.getMaterial("${quest.blockBreakTypeIdentifier}")
            if(parsedConfigBlock == event.block.type){
                QuestSystem.processQuestProgression(event.player.uniqueId, quest.id)
            }
        }
    }

}