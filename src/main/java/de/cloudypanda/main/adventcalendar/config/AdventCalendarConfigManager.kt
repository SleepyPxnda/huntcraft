package de.cloudypanda.main.adventcalendar.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.config.AbstractFileManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

class AdventCalendarConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<AdventCalendarConfigModel>(fileName, huntcraft, AdventCalendarConfigModel::class.java) {

    override fun afterInit() {
        setupAdventCalendar();
    }

    private fun setupAdventCalendar() {
        val adventCalendarConfigModel = AdventCalendarConfigModel();

        val day1Config = AdventCalendarDayConfig(
            "2024-11-24",
            "Welcome to the first day of the Advent Calendar! \n Today's challenge ist to submit a workbench",
            10,
            AdventCalendarChallengeType.ITEM,
            null,
            AdventCalendarSubmitItemConfig(
                Material.DIAMOND_SWORD,
                1,
                null,
                null,
                listOf(AdventCalendarSubmitItemEnchantConfig(Enchantment.SHARPNESS, 5))
            )
        );

        adventCalendarConfigModel.challenges.add(day1Config);
        this.saveToFile(adventCalendarConfigModel);
    }
}
