# Integrating the Main Application File (Huntcraft.kt) into DDD Architecture

This document outlines how to transform the current main application file (`Huntcraft.kt`) into a DDD-compliant main plugin class (`HuntcraftPlugin.kt`).

## Current Implementation Analysis

The current `Huntcraft.kt` file:

```kotlin
package de.cloudypanda.main

import de.cloudypanda.main.config.manager.CoreConfigManager
import de.cloudypanda.main.config.manager.DeathTimerConfigManager
import de.cloudypanda.main.core.event.CoreEventListener
import de.cloudypanda.main.core.tablist.TabListManager
import de.cloudypanda.main.deathtimer.DeathTimerEventListener
import org.bukkit.plugin.java.JavaPlugin

class Huntcraft : JavaPlugin() {

    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", this)
    val coreConfigManager = CoreConfigManager("hc_core", this)

    val tablistManager = TabListManager()

    companion object {
        lateinit var instance: Huntcraft
    }

    override fun onEnable() {
        instance = this
        //Create main Config file
        val coreConfig = coreConfigManager.createFileIfNotExists()

        //Register Core Events
        server.pluginManager.registerEvents(CoreEventListener(), this)

        if (coreConfig.deathTimer.enabled) {
            registerDeathTimerModule()
        }
    }

    private fun registerDeathTimerModule() {
        //Create config file
        deathTimerConfigManager.createFileIfNotExists()

        //Register Event Listener
        server.pluginManager.registerEvents(DeathTimerEventListener(this), this)
        this.componentLogger.info("DeathTimer Module is enabled")
    }
}
```

Key issues with the current implementation:

1. Uses a singleton pattern (`instance`) which makes testing difficult
2. Directly instantiates dependencies rather than injecting them
3. Mixes different concerns (configuration, event registration, etc.)
4. Lacks clear separation between layers

## DDD-Compliant Implementation

The proposed DDD-compliant `HuntcraftPlugin.kt`:

```kotlin
package de.cloudypanda.huntcraft

import de.cloudypanda.huntcraft.application.event.CoreEventListener
import de.cloudypanda.huntcraft.application.event.DeathTimerEventListener
import de.cloudypanda.huntcraft.application.service.DeathTimerApplicationService
import de.cloudypanda.huntcraft.domain.deathtimer.repository.UserTimeoutRepository
import de.cloudypanda.huntcraft.domain.deathtimer.service.DeathTimerService
import de.cloudypanda.huntcraft.domain.notification.service.NotificationService
import de.cloudypanda.huntcraft.infrastructure.config.repository.CoreConfigRepository
import de.cloudypanda.huntcraft.infrastructure.config.repository.DeathTimerConfigRepository
import de.cloudypanda.huntcraft.infrastructure.integration.discord.DiscordNotificationSender
import de.cloudypanda.huntcraft.infrastructure.persistence.file.FileUserTimeoutRepository
import org.bukkit.plugin.java.JavaPlugin

class HuntcraftPlugin : JavaPlugin() {

    // Repositories
    private lateinit var coreConfigRepository: CoreConfigRepository
    private lateinit var deathTimerConfigRepository: DeathTimerConfigRepository
    private lateinit var userTimeoutRepository: UserTimeoutRepository
    
    // Domain Services
    private lateinit var deathTimerService: DeathTimerService
    private lateinit var notificationService: NotificationService
    
    // Application Services
    private lateinit var deathTimerApplicationService: DeathTimerApplicationService
    
    override fun onEnable() {
        // Initialize repositories
        coreConfigRepository = CoreConfigRepository(this, "hc_core")
        deathTimerConfigRepository = DeathTimerConfigRepository(this, "hc_deathtimer")
        userTimeoutRepository = FileUserTimeoutRepository(deathTimerConfigRepository)
        
        // Initialize domain services
        deathTimerService = DeathTimerService(userTimeoutRepository)
        
        // Initialize notification infrastructure
        val coreConfig = coreConfigRepository.getConfig()
        val discordNotificationSender = DiscordNotificationSender(
            webhookUrl = coreConfig.webhook.webhookUrl,
            enabled = coreConfig.webhook.enabled
        )
        
        // Initialize notification service
        notificationService = NotificationService(discordNotificationSender)
        
        // Initialize application services
        deathTimerApplicationService = DeathTimerApplicationService(
            deathTimerService = deathTimerService,
            notificationService = notificationService,
            timeoutDurationMillis = coreConfig.deathTimer.timeoutDurationMillis
        )
        
        // Register event listeners
        server.pluginManager.registerEvents(CoreEventListener(), this)
        
        if (coreConfig.deathTimer.enabled) {
            server.pluginManager.registerEvents(
                DeathTimerEventListener(deathTimerApplicationService),
                this
            )
            logger.info("DeathTimer Module is enabled")
        }
    }
}
```

## Migration Plan

### 1. Create the New Package Structure

Ensure the DDD package structure is in place:

```
de.cloudypanda.huntcraft/
├── application/
│   ├── services/
│   └── event/
├── domain/
│   ├── core/
│   ├── player/
│   ├── deathtimer/
│   └── notification/
├── infrastructure/
│   ├── config/
│   ├── persistence/
│   └── integration/
└── HuntcraftPlugin.kt
```

### 2. Create Required Repositories

Implement the repository interfaces and their implementations:

- `CoreConfigRepository`
- `DeathTimerConfigRepository`
- `UserTimeoutRepository` and its implementation `FileUserTimeoutRepository`

### 3. Create Domain Services

Implement the domain services:

- `DeathTimerService`
- `NotificationService`

### 4. Create Application Services

Implement the application services:

- `DeathTimerApplicationService`

### 5. Create Event Listeners

Implement the event listeners:

- `CoreEventListener`
- `DeathTimerEventListener`

### 6. Implement the Main Plugin Class

Create the `HuntcraftPlugin.kt` file with the following structure:

1. Declare all dependencies as properties
2. Initialize repositories in `onEnable()`
3. Initialize domain services
4. Initialize application services
5. Register event listeners

### 7. Handle Singleton Pattern Transition

To handle the transition from the singleton pattern:

1. Create a simple service locator or dependency injection container
2. Register all services in the container during initialization
3. Provide a way for legacy code to access services through the container

Example service locator:

```kotlin
object ServiceLocator {
    private val services = mutableMapOf<Class<*>, Any>()
    
    fun <T : Any> register(serviceClass: Class<T>, service: T) {
        services[serviceClass] = service
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> get(serviceClass: Class<T>): T {
        return services[serviceClass] as T
    }
}
```

### 8. Update References in Other Files

For each file that references `Huntcraft.instance`:

1. Identify the specific service or functionality being accessed
2. Update the reference to use dependency injection or the service locator

Example:

Before:
```kotlin
val config = Huntcraft.instance.coreConfigManager.readFromFile()
```

After:
```kotlin
val config = ServiceLocator.get(CoreConfigRepository::class.java).getConfig()
```

### 9. Test the Implementation

Test the implementation to ensure:

1. All modules initialize correctly
2. All event listeners are registered properly
3. The death timer functionality works as expected

## Implementation Steps

1. **Create the HuntcraftPlugin.kt file**:
   - Place it in the root package `de.cloudypanda.huntcraft`
   - Implement the class structure as shown above

2. **Implement the initialization sequence**:
   - Follow the layered approach (repositories → domain services → application services → event listeners)
   - Ensure proper dependency injection

3. **Implement the service locator**:
   - Create a simple service locator for backward compatibility
   - Register all services during initialization

4. **Update plugin.yml**:
   - Update the main class reference to point to the new `HuntcraftPlugin` class

5. **Gradually migrate references**:
   - Start with the most critical components
   - Test after each migration step

## Benefits of the DDD Approach

1. **Clear Separation of Concerns**:
   - Each layer has a specific responsibility
   - Dependencies are explicit and injected

2. **Testability**:
   - Components can be tested in isolation
   - Dependencies can be mocked

3. **Maintainability**:
   - Code organization reflects business domains
   - Dependencies are explicit and managed

4. **Flexibility**:
   - Easy to add or modify features
   - Clear boundaries between components

5. **Scalability**:
   - New domains can be added without affecting existing ones
   - Infrastructure can be changed without affecting domain logic

## Conclusion

Transforming the main application file from `Huntcraft.kt` to a DDD-compliant `HuntcraftPlugin.kt` is a significant step in adopting Domain-Driven Design for the Huntcraft project. By following the migration plan outlined in this document, you can successfully integrate the main application file into the DDD architecture while maintaining backward compatibility and ensuring a smooth transition.