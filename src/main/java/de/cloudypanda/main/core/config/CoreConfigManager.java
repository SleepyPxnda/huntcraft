package de.cloudypanda.main.core.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.file.AbstractFileManager;

public class CoreConfigManager extends AbstractFileManager<CoreConfigModel> {
    public CoreConfigManager(String fileName, Huntcraft huntcraft) {
        super(fileName, huntcraft, CoreConfigModel.class);
    }
}
