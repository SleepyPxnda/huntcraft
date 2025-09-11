package de.cloudypanda.main.quest

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class QuestEntityKillEventListener() : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent){
        if(event.damageSource.causingEntity !is Player){
            return
        }

        val player = event.damageSource.causingEntity as Player
        QuestSystem.executeEntityKillEvent(player.uniqueId, event.entity)
    }
}