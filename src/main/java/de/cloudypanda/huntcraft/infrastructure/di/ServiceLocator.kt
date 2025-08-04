package de.cloudypanda.huntcraft.infrastructure.di

/**
 * A simple service locator for dependency injection.
 * This provides backward compatibility for code that previously used the singleton pattern.
 */
object ServiceLocator {
    private val services = mutableMapOf<Class<*>, Any>()
    
    /**
     * Registers a service with the service locator.
     * @param serviceClass The class of the service
     * @param service The service instance
     */
    fun <T : Any> register(serviceClass: Class<T>, service: T) {
        services[serviceClass] = service
    }
    
    /**
     * Gets a service from the service locator.
     * @param serviceClass The class of the service to get
     * @return The service instance
     * @throws IllegalStateException if the service is not registered
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(serviceClass: Class<T>): T {
        return services[serviceClass] as? T
            ?: throw IllegalStateException("Service ${serviceClass.simpleName} not registered")
    }
    
    /**
     * Checks if a service is registered with the service locator.
     * @param serviceClass The class of the service to check
     * @return true if the service is registered, false otherwise
     */
    fun <T : Any> isRegistered(serviceClass: Class<T>): Boolean {
        return services.containsKey(serviceClass)
    }
    
    /**
     * Clears all registered services.
     * Primarily used for testing.
     */
    fun clear() {
        services.clear()
    }
}