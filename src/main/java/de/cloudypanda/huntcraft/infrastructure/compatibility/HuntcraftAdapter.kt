package de.cloudypanda.huntcraft.infrastructure.compatibility

import de.cloudypanda.huntcraft.HuntcraftPlugin
import de.cloudypanda.main.Huntcraft
import de.cloudypanda.main.config.manager.CoreConfigManager
import de.cloudypanda.main.config.manager.DeathTimerConfigManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Adapter that allows the new HuntcraftPlugin to be used where a Huntcraft instance is expected.
 * This is part of the compatibility layer for transitioning to the DDD architecture.
 */
class HuntcraftAdapter(private val plugin: HuntcraftPlugin) : Huntcraft() {
    
    /**
     * The core config manager from the plugin.
     * This overrides the property in Huntcraft to delegate to the plugin's config manager.
     */
    override val coreConfigManager: CoreConfigManager
        get() = plugin.getService(CoreConfigManager::class.java)
    
    /**
     * The death timer config manager from the plugin.
     * This overrides the property in Huntcraft to delegate to the plugin's config manager.
     */
    override val deathTimerConfigManager: DeathTimerConfigManager
        get() = plugin.getService(DeathTimerConfigManager::class.java)
    
    /**
     * Overrides the onEnable method to do nothing, as the plugin handles this.
     */
    override fun onEnable() {
        // No-op, handled by plugin
    }
    
    /**
     * Overrides the onDisable method to do nothing, as the plugin handles this.
     */
    override fun onDisable() {
        // No-op, handled by plugin
    }
    
    /**
     * Companion object that provides a factory method for creating a HuntcraftAdapter.
     */
    companion object {
        /**
         * Creates a HuntcraftAdapter for the given JavaPlugin.
         * If the plugin is already a Huntcraft instance, it returns it directly.
         * If the plugin is a HuntcraftPlugin, it creates an adapter.
         * Otherwise, it throws an IllegalArgumentException.
         */
        fun create(plugin: JavaPlugin): Huntcraft {
            return when (plugin) {
                is Huntcraft -> plugin
                is HuntcraftPlugin -> HuntcraftAdapter(plugin)
                else -> throw IllegalArgumentException("Cannot create adapter for plugin of type ${plugin.javaClass.name}")
            }
        }
    }
}