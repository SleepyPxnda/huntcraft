package de.cloudypanda.main.adventcalendar.config;

data class AdventCalendarDayConfig(val date: String,
                                   val message: String,
                                   val points: Int,
                                   val itemToSubmit: AdventCalendarSubmitItemConfig) {}
