package de.cloudypanda.main.config.manager

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.manager.AbstractFileManager
import de.cloudypanda.main.config.deathtimer.DeathTimerConfigModel

class DeathTimerConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<DeathTimerConfigModel>(fileName, huntcraft, DeathTimerConfigModel::class.java)