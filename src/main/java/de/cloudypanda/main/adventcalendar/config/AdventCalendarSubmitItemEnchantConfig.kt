package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;


@JsonSerialize(using = SubmitItemEnchantSerializer::class)
@JsonDeserialize(using = SubmitItemEnchantDeserializer::class)
data class AdventCalendarSubmitItemEnchantConfig(val enchantment: Enchantment, val level: Int) {}
