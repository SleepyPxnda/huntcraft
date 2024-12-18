package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bukkit.enchantments.Enchantment


@JsonSerialize(using = SubmitItemEnchantSerializer::class)
@JsonDeserialize(using = SubmitItemEnchantDeserializer::class)
data class AdventCalendarSubmitItemEnchantConfig(val enchant: Enchantment, val level: Int) {}
