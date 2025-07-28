package de.cloudypanda.main.config.adventcalendar

data class AdventCalendarDayConfig(
    val date: String,
    val message: String,
    val points: Int,
    val achievementToSubmit: AdventCalendarSubmitAchievementConfig?,
    val itemToSubmit: AdventCalendarSubmitItemConfig?
) {
    constructor() : this("", "", 0,  null, null)
}
