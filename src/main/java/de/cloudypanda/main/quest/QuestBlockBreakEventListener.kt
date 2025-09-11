package de.cloudypanda.main.quest

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class QuestBlockBreakEventListener () : Listener {

    @EventHandler()
    fun onBlockBreak(event: BlockBreakEvent){
        QuestSystem.executeBlockBreakEvent(event.player.uniqueId, event.block)
    }
}