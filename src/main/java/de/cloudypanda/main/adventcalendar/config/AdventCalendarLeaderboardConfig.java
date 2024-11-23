package de.cloudypanda.main.adventcalendar.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class AdventCalendarLeaderboardConfig {
    private UUID playerID;
    private int points;
    private List<String> completedDays = new ArrayList<>();
}