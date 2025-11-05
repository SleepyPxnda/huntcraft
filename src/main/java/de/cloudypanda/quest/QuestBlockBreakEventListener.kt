package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class QuestBlockBreakEventListener () : Listener {

    @EventHandler()
    fun onBlockBreak(event: BlockBreakEvent){
        val player = PlayerManager.instance.getPlayerByUUID(event.player.uniqueId) ?: return
        player.executeBlockBreakEvent(event.player.uniqueId, event.block)
    }
}