package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.cloudypanda.main.common.file.SubmitItemEnchantDeserializer;
import de.cloudypanda.main.common.file.SubmitItemEnchantSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@JsonSerialize(using = SubmitItemEnchantSerializer.class)
@JsonDeserialize(using = SubmitItemEnchantDeserializer.class)
public class AdventCalendarSubmitItemEnchantConfig {
    private Enchantment enchant;
    private int level;
}
