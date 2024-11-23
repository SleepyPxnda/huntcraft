package de.cloudypanda.main.deathtimer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.file.AbstractFileManager;

import java.io.IOException;

public class DeathTimerConfigManager extends AbstractFileManager<DeathTimerConfigModel> {

    public DeathTimerConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft);
    }

    @Override
    public DeathTimerConfigModel readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(super.getFilePath().toFile(), DeathTimerConfigModel.class);
        } catch (IOException e) {
            super.getHuntcraft().getComponentLogger().error("Something went wrong reading from file. {}", e.getMessage());
        }
        return new DeathTimerConfigModel();
    }
}
