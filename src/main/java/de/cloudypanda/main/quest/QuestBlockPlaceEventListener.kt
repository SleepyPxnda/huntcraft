package de.cloudypanda.main.quest

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class QuestBlockPlaceEventListener() : Listener {

    @EventHandler()
    fun onBlockPlace(event: BlockPlaceEvent){
        QuestSystem.executeBlockPlaceEvent(event.player.uniqueId, event.block)
    }
}