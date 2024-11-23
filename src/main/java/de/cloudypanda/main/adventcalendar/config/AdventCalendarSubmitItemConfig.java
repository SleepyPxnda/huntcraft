package de.cloudypanda.main.adventcalendar.config;

import lombok.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class AdventCalendarSubmitItemConfig {
    private @NonNull Material material;
    private @NonNull Integer amount;
    private Integer durability;
    private String name;
    private List<AdventCalendarSubmitItemEnchantConfig> enchants = new ArrayList<>();
}
