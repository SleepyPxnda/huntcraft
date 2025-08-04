# Migration Guide: Implementing Domain-Driven Design in Huntcraft

This guide provides a step-by-step approach to migrate the existing Huntcraft codebase to the new Domain-Driven Design (DDD) architecture.

## Why Domain-Driven Design?

Before diving into the migration steps, let's understand the key benefits of adopting DDD for the Huntcraft project:

1. **Business Focus**: DDD aligns code structure with the business domains, making the codebase more intuitive and easier to understand.

2. **Maintainability**: Clear boundaries between domains make it easier to maintain and extend the codebase.

3. **Testability**: Proper dependency injection and separation of concerns make the code more testable.

4. **Flexibility**: The modular structure allows for easier addition or modification of features.

5. **Scalability**: As the project grows, new domains can be added without affecting existing ones.

## Migration Strategy

The migration will follow an incremental approach, focusing on one domain at a time to minimize disruption to the existing functionality.

### Phase 1: Setup Project Structure

1. Create the new package structure according to the DDD architecture plan:
   ```
   de.cloudypanda.huntcraft/
   ├── application/
   ├── domain/
   ├── infrastructure/
   └── HuntcraftPlugin.kt
   ```

2. Create a simple dependency injection mechanism to replace the singleton pattern.

### Phase 2: Migrate Core Domain

1. Create domain models, services, and repositories for the Core domain.
2. Implement infrastructure components for the Core domain.
3. Create application services and event listeners for the Core domain.
4. Update the main plugin class to use the new Core domain components.
5. Test the Core domain functionality to ensure it works as expected.

### Phase 3: Migrate Death Timer Domain

1. Create domain models, services, and repositories for the Death Timer domain.
2. Implement infrastructure components for the Death Timer domain.
3. Create application services and event listeners for the Death Timer domain.
4. Update the main plugin class to use the new Death Timer domain components.
5. Test the Death Timer domain functionality to ensure it works as expected.

### Phase 4: Migrate Notification Domain

1. Create domain models, services, and repositories for the Notification domain.
2. Implement infrastructure components for the Notification domain.
3. Create application services for the Notification domain.
4. Update the main plugin class to use the new Notification domain components.
5. Test the Notification domain functionality to ensure it works as expected.

### Phase 5: Migrate Player Management Domain

1. Create domain models, services, and repositories for the Player Management domain.
2. Implement infrastructure components for the Player Management domain.
3. Create application services and event listeners for the Player Management domain.
4. Update the main plugin class to use the new Player Management domain components.
5. Test the Player Management domain functionality to ensure it works as expected.

### Phase 6: Final Integration and Testing

1. Remove any remaining references to the old architecture.
2. Perform comprehensive testing to ensure all functionality works as expected.
3. Update documentation to reflect the new architecture.

## Detailed Migration Steps

### Step 1: Create Basic DI Container

Create a simple dependency injection container to manage service instances:

```kotlin
// infrastructure/di/ServiceLocator.kt
class ServiceLocator {
    private val services = mutableMapOf<Class<*>, Any>()
    
    inline fun <reified T : Any> register(service: T) {
        services[T::class.java] = service
    }
    
    inline fun <reified T : Any> get(): T {
        return services[T::class.java] as T
    }
}
```

### Step 2: Migrate Domain Models

For each domain, create the appropriate domain models. For example, for the Death Timer domain:

```kotlin
// domain/deathtimer/model/UserTimeout.kt
class UserTimeout(
    val playerUUID: UUID,
    val playerName: String,
    val deathTimestamp: Instant,
    val timeoutDurationMillis: Long
) {
    fun isAllowedToJoin(): Boolean {
        val currentTime = Instant.now()
        val timeoutEndTime = deathTimestamp.plusMillis(timeoutDurationMillis)
        return currentTime.isAfter(timeoutEndTime)
    }
    
    // Other methods...
}
```

### Step 3: Create Repository Interfaces

Define repository interfaces in the domain layer:

```kotlin
// domain/deathtimer/repository/UserTimeoutRepository.kt
interface UserTimeoutRepository {
    fun save(userTimeout: UserTimeout)
    fun findByPlayerUUID(playerUUID: UUID): UserTimeout?
    fun findAll(): List<UserTimeout>
    fun remove(playerUUID: UUID)
}
```

### Step 4: Implement Domain Services

Create domain services that encapsulate business logic:

```kotlin
// domain/deathtimer/service/DeathTimerService.kt
class DeathTimerService(
    private val userTimeoutRepository: UserTimeoutRepository
) {
    fun recordPlayerDeath(playerUUID: UUID, playerName: String, timeoutDurationMillis: Long) {
        // Implementation...
    }
    
    fun canPlayerJoin(playerUUID: UUID): Boolean {
        // Implementation...
    }
    
    // Other methods...
}
```

### Step 5: Implement Repository Implementations

Create repository implementations in the infrastructure layer:

```kotlin
// infrastructure/persistence/file/FileUserTimeoutRepository.kt
class FileUserTimeoutRepository(
    private val deathTimerConfigRepository: DeathTimerConfigRepository
) : UserTimeoutRepository {
    // Implementation...
}
```

### Step 6: Create Application Services

Create application services that orchestrate use cases:

```kotlin
// application/service/DeathTimerApplicationService.kt
class DeathTimerApplicationService(
    private val deathTimerService: DeathTimerService,
    private val notificationService: NotificationService,
    private val timeoutDurationMillis: Long
) {
    // Implementation...
}
```

### Step 7: Update Event Listeners

Refactor event listeners to use the new application services:

```kotlin
// application/event/DeathTimerEventListener.kt
class DeathTimerEventListener(
    private val deathTimerApplicationService: DeathTimerApplicationService
) : Listener {
    // Implementation...
}
```

### Step 8: Update Main Plugin Class

Refactor the main plugin class to use the new architecture:

```kotlin
// HuntcraftPlugin.kt
class HuntcraftPlugin : JavaPlugin() {
    private val serviceLocator = ServiceLocator()
    
    override fun onEnable() {
        // Initialize repositories
        // Initialize domain services
        // Initialize application services
        // Register event listeners
    }
}
```

## Testing Strategy

For each migrated domain, implement the following testing strategy:

1. **Unit Tests**: Test domain models and services in isolation.
2. **Integration Tests**: Test the interaction between domain services and repositories.
3. **End-to-End Tests**: Test the complete functionality from event listeners to domain services.

## Common Pitfalls and How to Avoid Them

1. **Tight Coupling**: Avoid direct dependencies between domains. Use interfaces and dependency injection.

2. **Anemic Domain Models**: Ensure domain models contain business logic, not just data.

3. **Infrastructure Leakage**: Keep infrastructure concerns (like file I/O) out of the domain layer.

4. **Overengineering**: Start with a simple DDD structure and refine it as needed.

5. **Neglecting Tests**: Write tests for each component to ensure functionality is preserved during migration.

## Conclusion

Migrating to a Domain-Driven Design architecture is a significant undertaking, but the benefits in terms of maintainability, testability, and scalability make it worthwhile. By following this guide and taking an incremental approach, you can successfully transform the Huntcraft codebase into a well-structured, domain-focused application.

Remember that DDD is not just about code structure—it's about aligning your code with the business domains and creating a ubiquitous language that both developers and stakeholders can understand. As you migrate, focus on capturing the essential concepts and behaviors of each domain in your code.