package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AdventCalendarConfigModel {
    private List<AdventCalendarDayConfig> challenges = new ArrayList<>();
    private List<AdventCalendarLeaderboardConfigModel> leaderboard = new ArrayList<>();

    @JsonIgnore
    public AdventCalendarDayConfig getConfigForDay(LocalDate day) {
        return challenges.stream()
                .filter(dayConfig -> LocalDate.parse(dayConfig.getDate()).equals(LocalDate.now()))
                .findFirst().orElse(null);
    }
}
