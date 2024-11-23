package de.cloudypanda.main.adventcalendar.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdventCalendarDayConfig {
    private String date;
    private String message;
    private int points;
    private AdventCalendarSubmitItemConfig itemToSubmit;
}
