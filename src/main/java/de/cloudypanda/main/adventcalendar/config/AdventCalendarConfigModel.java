package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AdventCalendarConfigModel {
    private List<AdventCalendarDayConfig> challenges = new ArrayList<>();
    private List<AdventCalendarLeaderboardConfig> leaderboard = new ArrayList<>();

    @JsonIgnore
    public AdventCalendarDayConfig getConfigForDay(LocalDate day) {
        return challenges.stream()
                .filter(dayConfig -> LocalDate.parse(dayConfig.getDate()).equals(LocalDate.now()))
                .findFirst().orElse(null);
    }

    @JsonIgnore
    public boolean hasPlayerAlreadyCompletedDay(UUID playerID, LocalDate day) {
        AdventCalendarLeaderboardConfig player = leaderboard.stream()
                .filter(playerConfig -> playerConfig.getPlayerID().equals(playerID))
                .findFirst()
                .orElse(null);

        return player != null && player.getCompletedDays().contains(day.toString());
    }

    @JsonIgnore
    public AdventCalendarLeaderboardConfig setCompletedForPlayer(UUID playerID, LocalDate day, int points) {
        AdventCalendarLeaderboardConfig player = leaderboard.stream()
                .filter(playerConfig -> playerConfig.getPlayerID().equals(playerID))
                .findFirst()
                .orElse(null);

        if(player == null) {
            player = new AdventCalendarLeaderboardConfig();
            player.setPlayerID(playerID);
            player.setPoints(points);
            player.setCompletedDays(List.of(day.toString()));
            leaderboard.add(player);
        } else {
            player.getCompletedDays().add(day.toString());
            player.setPoints(player.getPoints() + points);
        }

        return player;
    }
}
