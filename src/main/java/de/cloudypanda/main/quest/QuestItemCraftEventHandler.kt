package de.cloudypanda.main.quest

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent

class QuestItemCraftEventHandler() : Listener {

    @EventHandler
    fun onCraftItemEvent(event: CraftItemEvent){
        val player = event.viewers.firstOrNull { it is Player } as? Player

        if(player == null){
            println("[QuestItemCraftEventHandler] No player found in crafting event viewers!")
            return
        }

        QuestSystem.executeItemCraftEvent(player.uniqueId, event.recipe.result)
    }
}