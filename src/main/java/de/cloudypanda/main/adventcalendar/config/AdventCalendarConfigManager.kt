package de.cloudypanda.main.adventcalendar.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.config.AbstractFileManager;
import org.bukkit.Material;
import java.time.LocalDate

class AdventCalendarConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<AdventCalendarConfigModel>(fileName, huntcraft, AdventCalendarConfigModel::class.java) {

    override fun afterInit() {
        setupAdventCalendar();
    }

    private fun setupAdventCalendar() {
        val adventCalendarConfigModel = AdventCalendarConfigModel();

        val templateConfig = AdventCalendarDayConfig(
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

        adventCalendarConfigModel.challenges.add(templateConfig);
        this.saveToFile(adventCalendarConfigModel);
    }
}
