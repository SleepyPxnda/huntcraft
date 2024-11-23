package de.cloudypanda.main.adventcalendar.config;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AdventCalendarLeaderboardConfigModel {
    private UUID playerID;
    private int points;
    private List<LocalDate> completedDays;
}
