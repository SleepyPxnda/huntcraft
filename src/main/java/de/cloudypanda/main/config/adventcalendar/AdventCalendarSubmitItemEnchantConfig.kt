package de.cloudypanda.main.config.adventcalendar

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bukkit.enchantments.Enchantment


@JsonSerialize(using = SubmitItemEnchantSerializer::class)
@JsonDeserialize(using = SubmitItemEnchantDeserializer::class)
data class AdventCalendarSubmitItemEnchantConfig(val enchant: Enchantment, val level: Int) {}
