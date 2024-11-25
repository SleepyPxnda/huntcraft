package de.cloudypanda.main.core.event

import de.cloudypanda.main.Huntcraft
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent


class CoreEventListener(val huntcraft: Huntcraft) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(e: PlayerJoinEvent) {
        if (e.player.firstPlayed == 0L) {
            //TODO: Send CreatePlayer Request to Server
        }
    }
}