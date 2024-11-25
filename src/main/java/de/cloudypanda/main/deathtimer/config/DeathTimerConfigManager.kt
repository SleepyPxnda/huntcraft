package de.cloudypanda.main.deathtimer.config;

import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.common.config.AbstractFileManager;

class DeathTimerConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<DeathTimerConfigModel>(fileName, huntcraft, DeathTimerConfigModel::class.java) {
}
