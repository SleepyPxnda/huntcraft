package de.cloudypanda.main.adventcalendar.config;

import java.util.*

data class AdventCalendarLeaderboardConfig(
    val playerID: UUID,
    var points: Int,
    val completedDays: MutableList<String> = ArrayList()
) {
}
