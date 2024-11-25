package de.cloudypanda.main.core.tablist

import de.cloudypanda.main.Huntcraft
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.entity.Player
import java.time.LocalDate

class TablistDisplay {
    fun updateTablist(player: Player) {
        Huntcraft.instance.componentLogger.debug("Updating tablist for player ${player.name}")
        val headerline : Component = Component.newline()
            .append(Component.text(" ☁ "))
            .append(Component.text("Smoothcloud ").color(color(128,128,128)))
            .append(Component.text("X-MAS ").color(color(255, 0, 0)))
            .append(Component.text("Event").color(color(128,128,128)))
            .append(Component.newline())
            .append(Component.newline())

        val challenge = Huntcraft.instance.adventCalendarConfig.getConfigForDay(LocalDate.now())
        val hasPlayerCompleted = Huntcraft.instance.adventCalendarConfig.hasPlayerAlreadyCompletedDay(player.uniqueId, LocalDate.now())

        val challengeStatusText = if (hasPlayerCompleted) "Completed" else "Not completed"
        val challengeStatusColor = if (hasPlayerCompleted) color(0, 255, 0) else color(255, 0, 0)

        val challengeline = Component.newline()
            .append(Component.text("Today's challenge").color(color(255, 153, 0)))
            .append(Component.newline())
            .append(Component.text(challenge?.message ?: "No challenge today!"))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Status: "))
            .append(Component.text(challengeStatusText).color(challengeStatusColor))

        player.sendPlayerListHeaderAndFooter(headerline, challengeline)
    }
}