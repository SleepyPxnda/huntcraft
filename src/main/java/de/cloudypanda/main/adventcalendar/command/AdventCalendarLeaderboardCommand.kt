package de.cloudypanda.main.adventcalendar.command;

import de.cloudypanda.main.Huntcraft
import io.papermc.paper.command.brigadier.BasicCommand
import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

class AdventCalendarLeaderboardCommand() : BasicCommand {

    override fun execute(commandSourceStack: CommandSourceStack, args: Array<out String>) {
        if (EntityType.PLAYER != (commandSourceStack.getExecutor()?.type ?: EntityType.UNKNOWN)) {
            return;
        }

        val adventCalendarConfigModel = Huntcraft.adventCalendarConfig;

        val player = commandSourceStack.getExecutor() as Player;

        if (adventCalendarConfigModel.leaderboard.isEmpty()) {
            player.sendMessage("Leaderboard is empty");
            return;
        }

        player.sendMessage(" *** Leaderboard *** ");

        adventCalendarConfigModel.leaderboard.sortedBy { it.points }.forEach { leaderboardConfig ->
            val position = adventCalendarConfigModel.leaderboard.indexOf(leaderboardConfig) + 1;
            player.sendMessage(Bukkit.getOfflinePlayer(position.toString() + " | + " + leaderboardConfig.playerID).name + " - " + leaderboardConfig.points);
        }
    }
}
