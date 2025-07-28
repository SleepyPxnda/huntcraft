package de.cloudypanda.main.config.manager

import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.manager.AbstractFileManager
import de.cloudypanda.main.config.core.CoreConfigModel

class CoreConfigManager(fileName: String, huntcraft: Huntcraft) :
    AbstractFileManager<CoreConfigModel>(fileName, huntcraft, CoreConfigModel::class.java)