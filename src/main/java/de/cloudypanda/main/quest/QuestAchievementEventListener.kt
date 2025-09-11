package de.cloudypanda.main.quest

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class QuestAchievementEventListener() : Listener {

    @EventHandler
    fun onPlayerAdvancementDoneEvent(event: PlayerAdvancementDoneEvent){

        val progress = event.player.getAdvancementProgress(event.advancement)

        if(progress.isDone){
            QuestSystem.executeAchievementEvent(event.player.uniqueId, event.advancement.key().toString())
        }
    }
}