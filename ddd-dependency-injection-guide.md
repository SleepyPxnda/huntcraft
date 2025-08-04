# Dependency Injection Guide for Huntcraft

This guide demonstrates how to update code that uses the Huntcraft singleton pattern to use dependency injection through the ServiceLocator. This is part of the migration to a Domain-Driven Design (DDD) architecture.

## Current Singleton Pattern

Currently, many parts of the codebase access the Huntcraft instance and its services through the singleton pattern:

```kotlin
// Accessing the Huntcraft instance
val plugin = Huntcraft.instance

// Accessing configuration managers
val coreConfig = Huntcraft.instance.coreConfigManager.readFromFile()
val deathTimerConfig = Huntcraft.instance.deathTimerConfigManager.readFromFile()

// Accessing other services
val tablistManager = Huntcraft.instance.tablistManager
```

This pattern creates tight coupling between components and makes testing difficult.

## Transitional Approach

During the transition to DDD, we're using a compatibility layer that allows existing code to continue working while we gradually migrate to dependency injection. The HuntcraftPlugin class creates a Huntcraft instance and sets it as the singleton instance, so existing code that uses `Huntcraft.instance` will continue to work.

However, new code and refactored code should use dependency injection through the ServiceLocator.

## Using the ServiceLocator

The ServiceLocator provides a way to access services without using the singleton pattern:

```kotlin
// Accessing the Huntcraft instance
val plugin = ServiceLocator.get(Huntcraft::class.java)

// Accessing configuration managers
val coreConfigManager = ServiceLocator.get(CoreConfigManager::class.java)
val coreConfig = coreConfigManager.readFromFile()

val deathTimerConfigManager = ServiceLocator.get(DeathTimerConfigManager::class.java)
val deathTimerConfig = deathTimerConfigManager.readFromFile()

// Accessing other services
val tablistManager = ServiceLocator.get(TabListManager::class.java)
```

## Using Constructor Injection

For new classes or refactored classes, use constructor injection to make dependencies explicit:

```kotlin
// Before
class MyClass {
    fun doSomething() {
        val config = Huntcraft.instance.coreConfigManager.readFromFile()
        // Use config
    }
}

// After
class MyClass(private val coreConfigManager: CoreConfigManager) {
    fun doSomething() {
        val config = coreConfigManager.readFromFile()
        // Use config
    }
}

// Usage
val myClass = MyClass(ServiceLocator.get(CoreConfigManager::class.java))
```

## Examples of Updating References

### Example 1: Event Listener

```kotlin
// Before
class MyEventListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val config = Huntcraft.instance.coreConfigManager.readFromFile()
        // Use config
    }
}

// After
class MyEventListener(private val coreConfigManager: CoreConfigManager) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val config = coreConfigManager.readFromFile()
        // Use config
    }
}

// Registration
server.pluginManager.registerEvents(
    MyEventListener(ServiceLocator.get(CoreConfigManager::class.java)),
    this
)
```

### Example 2: Command Executor

```kotlin
// Before
class MyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val config = Huntcraft.instance.coreConfigManager.readFromFile()
        // Use config
        return true
    }
}

// After
class MyCommand(private val coreConfigManager: CoreConfigManager) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val config = coreConfigManager.readFromFile()
        // Use config
        return true
    }
}

// Registration
getCommand("mycommand")?.executor = MyCommand(ServiceLocator.get(CoreConfigManager::class.java))
```

### Example 3: Utility Class

```kotlin
// Before
class MyUtil {
    companion object {
        fun doSomething() {
            val config = Huntcraft.instance.coreConfigManager.readFromFile()
            // Use config
        }
    }
}

// After - Option 1: Instance method
class MyUtil(private val coreConfigManager: CoreConfigManager) {
    fun doSomething() {
        val config = coreConfigManager.readFromFile()
        // Use config
    }
}

// After - Option 2: Static method with ServiceLocator
class MyUtil {
    companion object {
        fun doSomething() {
            val coreConfigManager = ServiceLocator.get(CoreConfigManager::class.java)
            val config = coreConfigManager.readFromFile()
            // Use config
        }
    }
}
```

## Benefits of Dependency Injection

1. **Explicit Dependencies**: Dependencies are declared explicitly, making the code more readable and maintainable.
2. **Testability**: Components can be tested in isolation by providing mock dependencies.
3. **Flexibility**: Dependencies can be swapped out without changing the code that uses them.
4. **Decoupling**: Components are decoupled from each other, making the codebase more modular.

## Gradual Migration

The migration to dependency injection should be gradual:

1. Start with new code and use dependency injection from the beginning.
2. When modifying existing code, update it to use dependency injection.
3. Focus on one component at a time, ensuring it works correctly before moving on.
4. Eventually, remove the compatibility layer and the singleton pattern entirely.

## Conclusion

By following this guide, you can gradually migrate the Huntcraft codebase from the singleton pattern to dependency injection, making it more maintainable, testable, and aligned with DDD principles.