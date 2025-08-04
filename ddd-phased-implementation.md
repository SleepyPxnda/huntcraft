# Phased Implementation Approach for DDD Architecture in Huntcraft

This document outlines a phased approach to implementing Domain-Driven Design (DDD) architecture in the Huntcraft project, with a focus on ensuring compatibility between the old and new architecture during the transition.

## Phase 1: Infrastructure Setup and Compatibility Layer

### 1.1 Create Basic DDD Infrastructure

- Create the package structure for DDD architecture
- Implement the ServiceLocator for dependency injection
- Create basic interfaces for repositories and services

### 1.2 Create Compatibility Layer

To address the compatibility issues between the old and new architecture, we need to create a compatibility layer that allows the existing code to work with the new architecture. There are several approaches:

#### Option 1: Adapter Pattern

Create adapter classes that convert between the old and new architecture:

```kotlin
/**
 * Adapter that allows new code to work with old Huntcraft class.
 */
class HuntcraftAdapter(private val huntcraft: de.cloudypanda.main.Huntcraft) {
    fun getCoreConfigManager(): CoreConfigManager = huntcraft.coreConfigManager
    fun getDeathTimerConfigManager(): DeathTimerConfigManager = huntcraft.deathTimerConfigManager
    // Other methods as needed
}

/**
 * Adapter that allows old code to work with new HuntcraftPlugin class.
 */
class HuntcraftPluginAdapter(private val plugin: HuntcraftPlugin) : de.cloudypanda.main.Huntcraft() {
    // Override methods to delegate to the plugin
    override fun onEnable() {
        // No-op, handled by plugin
    }
    
    // Other overrides as needed
}
```

#### Option 2: Inheritance (if possible)

If Huntcraft is not final, make HuntcraftPlugin extend Huntcraft:

```kotlin
class HuntcraftPlugin : de.cloudypanda.main.Huntcraft() {
    // Override methods as needed
}
```

#### Option 3: Plugin Registration Approach

Register the new plugin in the old plugin's instance field:

```kotlin
// In HuntcraftPlugin.onEnable()
de.cloudypanda.main.Huntcraft.instance = createHuntcraftInstance()

private fun createHuntcraftInstance(): de.cloudypanda.main.Huntcraft {
    // Create and configure a Huntcraft instance
    val huntcraft = de.cloudypanda.main.Huntcraft()
    // Set up huntcraft instance
    return huntcraft
}
```

### 1.3 Update plugin.yml

Update the plugin.yml file to use the new main class:

```yaml
name: Huntcraft
version: 1.0.0
main: de.cloudypanda.huntcraft.HuntcraftPlugin
api-version: 1.19
```

## Phase 2: Domain Model Implementation

### 2.1 Implement Core Domain

- Create domain models for the Core domain
- Implement domain services for the Core domain
- Create repository interfaces for the Core domain

### 2.2 Implement Death Timer Domain

- Create domain models for the Death Timer domain
- Implement domain services for the Death Timer domain
- Create repository interfaces for the Death Timer domain

### 2.3 Implement Notification Domain

- Create domain models for the Notification domain
- Implement domain services for the Notification domain
- Create repository interfaces for the Notification domain

## Phase 3: Infrastructure Implementation

### 3.1 Implement Repository Implementations

- Implement file-based repositories for configuration
- Implement repositories for domain models

### 3.2 Implement External Service Adapters

- Implement Discord integration adapters
- Implement other external service adapters as needed

## Phase 4: Application Layer Implementation

### 4.1 Implement Application Services

- Create application services that orchestrate use cases
- Implement event listeners that use application services

### 4.2 Update Main Plugin Class

- Update HuntcraftPlugin to use the new application services
- Remove compatibility layer code that is no longer needed

## Phase 5: Migration of Existing Code

### 5.1 Identify References to Singleton

Identify all places in the codebase that reference the Huntcraft singleton:

```kotlin
// Example of code that uses the singleton
val config = Huntcraft.instance.coreConfigManager.readFromFile()
```

### 5.2 Update References to Use Dependency Injection

Update these references to use dependency injection or the service locator:

```kotlin
// Using service locator
val config = ServiceLocator.get(CoreConfigRepository::class.java).getConfig()

// Using dependency injection
class MyClass(private val coreConfigRepository: CoreConfigRepository) {
    fun doSomething() {
        val config = coreConfigRepository.getConfig()
    }
}
```

### 5.3 Remove Singleton Pattern

Once all references have been updated, remove the singleton pattern from the codebase.

## Phase 6: Testing and Refinement

### 6.1 Test Each Component

- Write unit tests for domain models and services
- Write integration tests for repositories and application services
- Write end-to-end tests for the complete functionality

### 6.2 Refine the Architecture

- Identify any issues or areas for improvement
- Refine the architecture based on feedback and testing
- Document the final architecture

## Addressing Compatibility Issues

### Issue 1: Type Mismatch in Constructors

The current implementation has type mismatches in constructors:

```
Argument type mismatch: actual type is 'HuntcraftPlugin', but 'Huntcraft' was expected.
```

#### Solution 1: Create Wrapper Classes

Create wrapper classes that adapt between the old and new types:

```kotlin
class ConfigManagerWrapper(private val plugin: JavaPlugin) {
    val coreConfigManager = CoreConfigManager("hc_core", plugin as? Huntcraft ?: createHuntcraftAdapter(plugin))
    val deathTimerConfigManager = DeathTimerConfigManager("hc_deathtimer", plugin as? Huntcraft ?: createHuntcraftAdapter(plugin))
    
    private fun createHuntcraftAdapter(plugin: JavaPlugin): Huntcraft {
        // Create an adapter that implements the Huntcraft interface
        // but delegates to the plugin
        return HuntcraftAdapter(plugin)
    }
}
```

#### Solution 2: Modify Existing Classes

If possible, modify the existing classes to accept JavaPlugin instead of Huntcraft:

```kotlin
// Before
class CoreConfigManager(filename: String, private val huntcraft: Huntcraft)

// After
class CoreConfigManager(filename: String, private val plugin: JavaPlugin)
```

#### Solution 3: Use Reflection

As a last resort, use reflection to set the instance field:

```kotlin
// Set the instance field using reflection
val instanceField = Huntcraft::class.java.getDeclaredField("instance")
instanceField.isAccessible = true
instanceField.set(null, huntcraftAdapter)
```

### Issue 2: Accessing Singleton Methods

Code that accesses methods on the singleton:

```kotlin
Huntcraft.instance.someMethod()
```

#### Solution: Delegate to Service Locator

Update the companion object to delegate to the service locator:

```kotlin
companion object {
    val instance: Huntcraft
        get() = ServiceLocator.get(Huntcraft::class.java)
}
```

## Implementation Timeline

1. **Week 1**: Set up infrastructure and compatibility layer
2. **Week 2-3**: Implement domain models and services
3. **Week 4**: Implement repository implementations
4. **Week 5**: Implement application services and event listeners
5. **Week 6**: Migrate existing code to use dependency injection
6. **Week 7-8**: Testing and refinement

## Conclusion

By following this phased implementation approach, we can gradually migrate the Huntcraft project to a DDD architecture while ensuring compatibility with the existing codebase. This approach minimizes disruption and allows for incremental improvements to the architecture.