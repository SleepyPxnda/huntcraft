package de.cloudypanda.quest

import de.cloudypanda.player.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class QuestAchievementEventListener() : Listener {

    @EventHandler
    fun onPlayerAdvancementDoneEvent(event: PlayerAdvancementDoneEvent){

        val progress = event.player.getAdvancementProgress(event.advancement)

        if(progress.isDone){
            val player = PlayerManager.instance.getPlayerByUUID(event.player.uniqueId) ?: return
            player.executeAchievementEvent(event.player.uniqueId, event.advancement.key().toString())
        }
    }
}