package de.cloudypanda.huntcraft

import de.cloudypanda.huntcraft.infrastructure.di.ServiceLocator
import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.manager.CoreConfigManager
import de.cloudypanda.main.config.manager.DeathTimerConfigManager
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.core.tablist.TabListManager
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main plugin class for Huntcraft.
 * This is a transitional implementation that bridges the old architecture with the new DDD architecture.
 * It uses the ServiceLocator for dependency injection while still working with the existing code structure.
 * It also creates a Huntcraft instance and sets it as the singleton instance for backward compatibility.
 */
class HuntcraftPlugin : JavaPlugin() {

    // The Huntcraft instance for backward compatibility
    private lateinit var huntcraftInstance: Huntcraft
    
    override fun onEnable() {
        // Create and initialize the Huntcraft instance
        createHuntcraftInstance()
        
        // Initialize the service locator
        initializeServiceLocator()
        
        // Load configuration
        val coreConfig = huntcraftInstance.coreConfigManager.createFileIfNotExists()
        
        // Register core event listener
        server.pluginManager.registerEvents(CoreEventListener(), this)
        
        // Conditionally enable death timer module
        if (coreConfig.deathTimer.enabled) {
            enableDeathTimerModule()
        }
        
        logger.info("Huntcraft plugin has been enabled with transitional DDD architecture")
    }
    
    /**
     * Creates and initializes the Huntcraft instance.
     * This provides backward compatibility with the existing code.
     */
    private fun createHuntcraftInstance() {
        // Create a new Huntcraft instance
        huntcraftInstance = Huntcraft()
        
        // Set it as the singleton instance
        Huntcraft.instance = huntcraftInstance
        
        // Initialize the Huntcraft instance (but don't call onEnable)
        // This is done manually to avoid duplicate initialization
        // We're essentially extracting the initialization logic from Huntcraft.onEnable()
        // without actually calling it
    }
    
    /**
     * Initializes the service locator with the plugin instance and core services.
     * This provides backward compatibility for code that previously used the singleton pattern.
     */
    private fun initializeServiceLocator() {
        // Register this plugin instance
        ServiceLocator.register(JavaPlugin::class.java, this)
        ServiceLocator.register(HuntcraftPlugin::class.java, this)
        
        // Register the Huntcraft instance
        ServiceLocator.register(Huntcraft::class.java, huntcraftInstance)
        
        // Register configuration managers
        ServiceLocator.register(DeathTimerConfigManager::class.java, huntcraftInstance.deathTimerConfigManager)
        ServiceLocator.register(CoreConfigManager::class.java, huntcraftInstance.coreConfigManager)
        
        // Register tab list manager
        ServiceLocator.register(TabListManager::class.java, huntcraftInstance.tablistManager)
    }
    
    /**
     * Enables the death timer module.
     */
    private fun enableDeathTimerModule() {
        // Create death timer config file
        huntcraftInstance.deathTimerConfigManager.createFileIfNotExists()
        
        // Register death timer event listener
        server.pluginManager.registerEvents(DeathTimerEventListener(huntcraftInstance), this)
        logger.info("DeathTimer Module is enabled")
    }
    
    /**
     * Gets a service from the service locator.
     * This provides a way for code to access services without using the singleton pattern.
     */
    fun <T : Any> getService(serviceClass: Class<T>): T {
        return ServiceLocator.get(serviceClass)
    }
    
    override fun onDisable() {
        logger.info("Huntcraft plugin has been disabled")
    }
    
    /**
     * Companion object that provides backward compatibility with the old singleton pattern.
     * This should be used only during the transition period and eventually removed.
     */
    companion object {
        /**
         * Gets the plugin instance.
         * This provides backward compatibility with the old singleton pattern.
         * @return The plugin instance
         */
        val instance: HuntcraftPlugin
            get() = ServiceLocator.get(HuntcraftPlugin::class.java)
    }
}