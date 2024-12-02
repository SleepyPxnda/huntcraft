package de.cloudypanda.main.adventcalendar.command

import de.cloudypanda.main.Huntcraft
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor.color
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class AdventCalendarLeaderboardCommand : BasicCommand {
    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        if (EntityType.PLAYER != commandSourceStack.executor.type) {
            return
        }

        val player = commandSourceStack.executor as Player

        val adventCalendarConfig = Huntcraft.instance.adventCalendarConfigManager.readFromFile();

        val leaderboard = adventCalendarConfig.history
            .sortedByDescending { it.points }
            .take(10)
            .mapIndexed { index, entry -> Component.text("${index + 1}. ${entry.playerID?.let { Bukkit.getOfflinePlayer(it).name }}: ${entry.points} | ${entry.completedDays.size} Days")}

        var finalMessage = Component.text("Leaderboard:").color(color(255, 153, 0))
            .append(Component.newline())
            .append(Component.newline())

        leaderboard.forEach {
            finalMessage = finalMessage.append(it.color(color(128, 128, 128))).append(Component.newline())
        }

        player.sendMessage(finalMessage);
    }
}