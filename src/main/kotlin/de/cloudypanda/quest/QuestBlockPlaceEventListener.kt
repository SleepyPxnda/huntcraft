package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class QuestBlockPlaceEventListener() : Listener {

    @EventHandler()
    fun onBlockPlace(event: BlockPlaceEvent){
        val player = PlayerManager.instance.getPlayerByUUID(event.player.uniqueId) ?: return
        player.executeBlockPlaceEvent(event.player.uniqueId, event.block)
    }
}