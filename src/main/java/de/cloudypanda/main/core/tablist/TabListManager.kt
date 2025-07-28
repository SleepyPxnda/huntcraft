package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.util.TextUtil
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class TabListManager() {
    companion object {
        fun updateAllPlayerTabList() {
            Bukkit.getOnlinePlayers().forEach { player ->
                player.sendPlayerListHeaderAndFooter(TextUtil.getTablistHeader(), Component.empty())
            }
        }
    }
}