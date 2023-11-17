package de.cloudypanda.main.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class DateUtil {

    public static String getFormattedStringForDateAfterMillis(long death, long timeout){
        String pattern = "yyyy-MM-dd HH-mm-ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(Date.from(Instant.ofEpochMilli(death).plusMillis(timeout)));
    }

    public static String getFormattedDurationUntilJoin(long start, long timeout){
        Duration d = Duration.between(Instant.ofEpochMilli(start), Instant.ofEpochMilli(start + timeout));
        return DurationFormatUtils.formatDuration(d.toMillis(), "HH:mm:ss", true);
    }

    public static String getFormattedDurationUntilJoin(long now, long start, long timeout){
        Duration d = Duration.between(Instant.ofEpochMilli(now), Instant.ofEpochMilli(start + timeout));
        return DurationFormatUtils.formatDuration(d.toMillis(), "HH:mm:ss", true);
    }

}
