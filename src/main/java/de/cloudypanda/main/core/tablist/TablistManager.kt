package de.cloudypanda.main.core.tablist

import org.bukkit.Bukkit

class TablistManager() {
    private val tablistDisplay: TablistDisplay = TablistDisplay()

    fun updateAllPlayerTablist() {
        Bukkit.getOnlinePlayers().forEach { player ->
            tablistDisplay.updateTablist(player)
        }
    }
}