package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent

class QuestItemCraftEventHandler() : Listener {

    @EventHandler
    fun onCraftItemEvent(event: CraftItemEvent){
        val viewer = event.viewers.firstOrNull { it is Player } as? Player

        if(viewer == null){
            return
        }

        val player = PlayerManager.getPlayerByUUID(viewer.uniqueId) ?: return
        player.executeItemCraftEvent(player.uuid, event.recipe.result)
    }
}