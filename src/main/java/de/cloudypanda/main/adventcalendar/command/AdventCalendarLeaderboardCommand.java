package de.cloudypanda.main.adventcalendar.command;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.adventcalendar.config.AdventCalendarConfigModel;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class AdventCalendarLeaderboardCommand implements BasicCommand {

    private final Huntcraft huntcraft;

    public AdventCalendarLeaderboardCommand(Huntcraft huntcraft) {
        this.huntcraft = huntcraft;
    }
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        if(!EntityType.PLAYER.equals(commandSourceStack.getExecutor().getType())) {
            return;
        }

        AdventCalendarConfigModel adventCalendarConfigModel = huntcraft.adventCalendarConfigManager.readFromFile();

        Player player = (Player) commandSourceStack.getExecutor();

        if(adventCalendarConfigModel.getLeaderboard().isEmpty()) {
            player.sendMessage("Leaderboard is empty");
            return;
        }

        player.sendMessage(" *** Leaderboard *** ");

        adventCalendarConfigModel.getLeaderboard().sort((o1, o2) -> o2.getPoints() - o1.getPoints());

        adventCalendarConfigModel.getLeaderboard().forEach(leaderboardConfig -> {
            int position = adventCalendarConfigModel.getLeaderboard().indexOf(leaderboardConfig) + 1;
            player.sendMessage(Bukkit.getOfflinePlayer(position + " | + " + leaderboardConfig.getPlayerID()).getName() + " - " + leaderboardConfig.getPoints());
        });
    }
}
