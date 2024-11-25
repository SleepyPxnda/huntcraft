package de.cloudypanda.main.util;

import org.apache.commons.lang3.time.DurationFormatUtils
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.*

class DateUtil {

    companion object {
        fun getFormattedStringForDateAfterMillis(death: Long, timeout: Long): String {
            val pattern = "HH:mm:ss dd/MM/yyyy";
            val simpleDateFormat = SimpleDateFormat(pattern);
            return simpleDateFormat.format(Date.from(Instant.ofEpochMilli(death).plusMillis(timeout)));
        }

        fun getFormattedDurationUntilJoin(start: Long, timeout: Long): String {
            val duration = Duration.between(Instant.ofEpochMilli(start), Instant.ofEpochMilli(start + timeout));
            return DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss", true);
        }

        fun getFormattedDurationUntilJoin(now: Long, start: Long, timeout: Long): String {
            val duration = Duration.between(Instant.ofEpochMilli(now), Instant.ofEpochMilli(start + timeout));
            return DurationFormatUtils.formatDuration(duration.toMillis(), "HH:mm:ss", true);
        }
    }
}
