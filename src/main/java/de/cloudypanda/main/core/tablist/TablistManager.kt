package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TablistManager() {
    private val tablistDisplay: TablistDisplay = TablistDisplay()

    fun updatePlayerTablist(player: Player){
        tablistDisplay.updateTablist(player)
    }

    fun updateAllPlayerTablist() {
        Bukkit.getOnlinePlayers().forEach { player ->
            tablistDisplay.updateTablist(player)
        }
    }
}