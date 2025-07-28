package de.cloudypanda.main.config.adventcalendar

import java.util.*

data class AdventCalendarHistoryConfig(
    val playerID: UUID?,
    var points: Int,
    val completedDays: MutableList<String> = ArrayList()
) {
    constructor() : this(null, 0, ArrayList())
}
