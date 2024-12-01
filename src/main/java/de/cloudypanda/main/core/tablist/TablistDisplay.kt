package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.util.TextUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.entity.Player
import java.time.LocalDate

class TablistDisplay {
    fun updateTablist(player: Player) {
        var challengeline = Component.empty();

        if (Huntcraft.instance.coreConfigModel.adventCalendar.enabled) {
            val challenge = Huntcraft.instance.adventCalendarConfig.getConfigForDay(LocalDate.now())
            val hasPlayerCompleted = Huntcraft.instance.adventCalendarConfig.hasPlayerAlreadyCompletedDay(player.uniqueId, LocalDate.now())
            challengeline = TextUtil.getTablistFooter(challenge?.message, challenge?.points, hasPlayerCompleted);

            val calendarConfigModel = Huntcraft.instance.adventCalendarConfig;

            val playerPoints = calendarConfigModel.getPointsForPlayer(player.uniqueId);
            val playerCompletedIndicator = if (hasPlayerCompleted) "✅" else "❌";
            val playerCompletedIndicatorColor = if (hasPlayerCompleted) color(0, 255, 0) else color(255, 0, 0)

            player.displayName(
                Component.text(player.name)
                    .append(Component.text(" ($playerPoints)").color(color(200, 103, 0))
                    .append(Component.text(" $playerCompletedIndicator").color(playerCompletedIndicatorColor))))

            player.playerListName(Component.text(player.name)
                .append(Component.text(" ($playerPoints)").color(color(200, 103, 0))
                    .append(Component.text(" $playerCompletedIndicator").color(playerCompletedIndicatorColor))))
        }

        player.sendPlayerListHeaderAndFooter(TextUtil.getTablistHeader(), challengeline)
    }
}