package de.cloudypanda.main.adventcalendar.config;

import lombok.*;
import org.bukkit.Material;

@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class AdventCalendarSubmitItemConfig {
    private @NonNull Material material;
    private @NonNull Integer amount;
    private Integer durability;
}
