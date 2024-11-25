package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.util.TextUtil
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.time.LocalDate

class TablistDisplay {
    fun updateTablist(player: Player) {
        var challengeline = Component.empty();

        if (Huntcraft.instance.coreConfigModel.adventCalendar.enabled) {
            val challenge = Huntcraft.instance.adventCalendarConfig.getConfigForDay(LocalDate.now())
            val hasPlayerCompleted = Huntcraft.instance.adventCalendarConfig.hasPlayerAlreadyCompletedDay(player.uniqueId, LocalDate.now())
            challengeline = TextUtil.getTablistFooter(challenge?.message, hasPlayerCompleted);
        }

        player.sendPlayerListHeaderAndFooter(TextUtil.getTablistHeader(), challengeline)
    }
}