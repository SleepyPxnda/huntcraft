package de.cloudypanda.main.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.util.DeathTimerConfigModel;

import java.io.IOException;

public class DeathTimerConfigManager extends AbstractFileManager<DeathTimerConfigModel> {

    public DeathTimerConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft);
    }

    @Override
    public DeathTimerConfigModel readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        DeathTimerConfigModel model = null;
        try {
            model =  mapper.readValue(super.getFilePath().toFile(), DeathTimerConfigModel.class);
        } catch (IOException e) {
            super.getHuntcraft().getComponentLogger().error("Something went wrong reading from file. {}", e.getMessage());
        }
        return model;
    }
}
