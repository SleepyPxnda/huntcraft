package de.cloudypanda.main.deathtimer.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.file.AbstractFileManager;

public class DeathTimerConfigManager extends AbstractFileManager<DeathTimerConfigModel> {

    public DeathTimerConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft, DeathTimerConfigModel.class);
    }
}
