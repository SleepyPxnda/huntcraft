package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
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

        val causingEntity = event.damageSource.causingEntity as Player
        val player = PlayerManager.getPlayerByUUID(causingEntity.uniqueId) ?: return
        player.executeEntityKillEvent(causingEntity.uniqueId, event.entity)
    }
}