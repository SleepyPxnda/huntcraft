package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.cloudypanda.main.common.file.EnchantmentDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;

@NoArgsConstructor
@Getter
@Setter
public class AdventCalendarSubmitItemEnchantConfig {
    @JsonDeserialize(using = EnchantmentDeserializer.class)
    private Enchantment enchant;
    private int level;
}
