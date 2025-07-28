package de.cloudypanda.main.config.adventcalendar

import org.bukkit.Material

data class AdventCalendarSubmitItemConfig(
    val material: Material,
    val amount: Int,
    val durability: Int?,
    val name: String?,
    val enchants: List<AdventCalendarSubmitItemEnchantConfig> = ArrayList()
) {
    constructor() : this(Material.AIR, 0, null, null)
}
