package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate
import java.util.*

data class AdventCalendarConfigModel(
    val challenges: MutableList<AdventCalendarDayConfig> = ArrayList(),
    val history: MutableList<AdventCalendarHistoryConfig> = ArrayList()
) {

    @JsonIgnore
    fun getConfigForDay(day: LocalDate): AdventCalendarDayConfig? {
        return challenges.firstOrNull() { it.date == day.toString() }
    }

    @JsonIgnore
    fun hasPlayerAlreadyCompletedDay(playerID: UUID, day: LocalDate): Boolean {
        val player = history.firstOrNull() { it.playerID == playerID }

        return player != null && player.completedDays.contains(day.toString());
    }

    @JsonIgnore
    fun setCompletedForPlayer(playerID: UUID, points: Int, day: LocalDate): AdventCalendarHistoryConfig {
        var player = history.firstOrNull() { it.playerID == playerID }

        if (player == null) {
            player = AdventCalendarHistoryConfig(playerID, points, mutableListOf(day.toString()));
            history.add(player);
        } else {
            player.completedDays.add(day.toString());
            player.points += points;
        }

        return player;
    }

    @JsonIgnore
    fun getPointsForPlayer(playerID: UUID): Int {
        val player = history.firstOrNull() { it.playerID == playerID }
        return player?.points ?: 0;
    }
}
