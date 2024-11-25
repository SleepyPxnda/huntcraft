package de.cloudypanda.main.adventcalendar.config;

data class AdventCalendarDayConfig(
    val date: String,
    val message: String,
    val points: Int,
    val challengeType: AdventCalendarChallengeType,
    val achievementToSubmit: AdventCalendarSubmitAchievementConfig?,
    val itemToSubmit: AdventCalendarSubmitItemConfig?
) {
    constructor() : this("", "", 0, AdventCalendarChallengeType.ITEM, null, null)
}
