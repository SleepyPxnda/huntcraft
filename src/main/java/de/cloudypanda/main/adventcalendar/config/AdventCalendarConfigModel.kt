package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.util.*

data class AdventCalendarConfigModel(
    val challenges: MutableList<AdventCalendarDayConfig> = ArrayList(),
    val leaderboard: MutableList<AdventCalendarLeaderboardConfig> = ArrayList()
) {

    @JsonIgnore
    fun getConfigForDay(day: LocalDate): AdventCalendarDayConfig? {
        return challenges.firstOrNull() { it.date == day.toString() }
    }

    @JsonIgnore
    fun hasPlayerAlreadyCompletedDay(playerID: UUID, day: LocalDate): Boolean {
        val player = leaderboard.firstOrNull() { it.playerID == playerID }

        return player != null && player.completedDays.contains(day.toString());
    }

    @JsonIgnore
    fun setCompletedForPlayer(playerID: UUID, day: LocalDate, points: Int): AdventCalendarLeaderboardConfig {
        var player = leaderboard.firstOrNull() { it.playerID == playerID }

        if (player == null) {
            player = AdventCalendarLeaderboardConfig(playerID, points, mutableListOf(day.toString()));
            leaderboard.add(player);
        } else {
            player.completedDays.add(day.toString());
            player.points += points;
        }

        return player;
    }
}
