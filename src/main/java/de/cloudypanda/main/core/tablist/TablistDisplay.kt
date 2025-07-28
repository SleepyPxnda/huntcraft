package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.util.TextUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.entity.Player
import java.time.LocalDate

class TablistDisplay {
    fun updateTablist(player: Player) {
        var challengeLine = Component.empty()
        val coreConfig = Huntcraft.instance.coreConfigManager.readFromFile()

        if (coreConfig.adventCalendar.enabled) {
            val adventCalendarConfig = Huntcraft.instance.adventCalendarConfigManager.readFromFile()
            val challenge = adventCalendarConfig.getConfigForDay(LocalDate.now())
            val hasPlayerCompleted = adventCalendarConfig.hasPlayerAlreadyCompletedDay(player.uniqueId, LocalDate.now())
            challengeLine = TextUtil.getTablistFooter(challenge?.message, challenge?.points, hasPlayerCompleted)

            val playerPoints = adventCalendarConfig.getPointsForPlayer(player.uniqueId)
            val playerCompletedIndicator = if (hasPlayerCompleted) "✅" else "❌"
            val playerCompletedIndicatorColor = if (hasPlayerCompleted) color(0, 255, 0) else color(255, 0, 0)

            player.displayName(
                Component.text(player.name)
                    .append(Component.text(" ($playerPoints)").color(color(200, 103, 0))
                    .append(Component.text(" $playerCompletedIndicator").color(playerCompletedIndicatorColor))))

            player.playerListName(Component.text(player.name)
                .append(Component.text(" ($playerPoints)").color(color(200, 103, 0))
                    .append(Component.text(" $playerCompletedIndicator").color(playerCompletedIndicatorColor))))
        }

        player.sendPlayerListHeaderAndFooter(TextUtil.getTablistHeader(), challengeLine)
    }
}