package de.cloudypanda.main.adventcalendar.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.config.AbstractFileManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import java.time.LocalDate

class AdventCalendarConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<AdventCalendarConfigModel>(fileName, huntcraft, AdventCalendarConfigModel::class.java) {

    override fun afterInit() {
        setupAdventCalendar();
    }

    private fun setupAdventCalendar() {
        val adventCalendarConfigModel = AdventCalendarConfigModel();

        val day1Config = AdventCalendarDayConfig(
            LocalDate.now().toString(),
            "Submit a Diamond Sword",
            10,
            null,
            AdventCalendarSubmitItemConfig(
                Material.DIAMOND_SWORD,
                1,
                null,
                null,
                listOf()
            )
        );

        adventCalendarConfigModel.challenges.add(day1Config);
        this.saveToFile(adventCalendarConfigModel);
    }
}
