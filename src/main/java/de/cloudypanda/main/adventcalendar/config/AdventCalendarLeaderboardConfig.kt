package de.cloudypanda.main.adventcalendar.config;

import java.util.*
import kotlin.collections.ArrayList

data class AdventCalendarLeaderboardConfig (val playerID: UUID, var points: Int, val completedDays: MutableList<String> = ArrayList()) {
}
