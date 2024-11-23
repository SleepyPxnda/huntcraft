package de.cloudypanda.main.adventcalendar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.file.AbstractFileManager;

import java.io.IOException;

public class AdventCalendarConfigManager extends AbstractFileManager<AdventCalendarConfigModel> {

    public AdventCalendarConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft);
    }

    @Override
    public AdventCalendarConfigModel readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(super.getFilePath().toFile(), AdventCalendarConfigModel.class);
        } catch (IOException e) {
            super.getHuntcraft().getComponentLogger().error("Something went wrong reading from file. {}", e.getMessage());
        }
        return new AdventCalendarConfigModel();
    }
}
