package de.cloudypanda.main.core.event

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.core.integrations.rest.RequestManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class CoreEventListener() : Listener {

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        if (e.player.firstPlayed == 0L) {
            RequestManager().createPlayer(e.player.uniqueId, e.player.name)
        }
    }
}