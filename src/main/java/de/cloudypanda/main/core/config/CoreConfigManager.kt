package de.cloudypanda.main.core.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.config.AbstractFileManager;

class CoreConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<CoreConfigModel>(fileName, huntcraft, CoreConfigModel::class.java) {
}
