package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.file.AbstractFileManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class AdventCalendarConfigManager extends AbstractFileManager<AdventCalendarConfigModel> {

    public AdventCalendarConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft, AdventCalendarConfigModel.class);
    }

    @Override
    public void afterInit() {
        setupAdventCalendar();
    }

    private void setupAdventCalendar() {
        AdventCalendarConfigModel adventCalendarConfigModel = new AdventCalendarConfigModel();

        AdventCalendarDayConfig day1Config = new AdventCalendarDayConfig();
        day1Config.setDate("2024-11-24");
        day1Config.setMessage("Welcome to the first day of the Advent Calendar! \n Today's challenge ist to submit a workbench");
        day1Config.setPoints(10);
        day1Config.setItemToSubmit(new AdventCalendarSubmitItemConfig(Material.DIAMOND_SWORD,
                1,
                null,
                null,
                List.of(new AdventCalendarSubmitItemEnchantConfig(Enchantment.SHARPNESS, 5))
        ));

        adventCalendarConfigModel.getChallenges().add(day1Config);

        this.saveToFile(adventCalendarConfigModel);
    }
}
