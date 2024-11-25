package de.cloudypanda.main.adventcalendar.config;

import java.util.*

data class AdventCalendarHistoryConfig(
    val playerID: UUID?,
    val completedDays: MutableList<String> = ArrayList()
) {
    constructor() : this(null, ArrayList())
}
