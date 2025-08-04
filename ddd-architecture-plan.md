# Domain-Driven Design Architecture for Huntcraft

## Identified Domains

Based on the analysis of the codebase, the following domains have been identified:

1. **Core Domain** - Essential plugin functionality
2. **Player Management Domain** - Player-related functionality including tab list
3. **Death Timer Domain** - Handling player deaths and enforcing timeouts
4. **Notification Domain** - Sending notifications to various platforms (Discord)
5. **Configuration Domain** - Managing plugin settings

## Proposed Package Structure

```
de.cloudypanda.huntcraft/
├── application/                  # Application layer - orchestrates use cases
│   ├── services/                 # Application services that coordinate domain operations
│   │   ├── CoreService.kt
│   │   ├── DeathTimerService.kt
│   │   └── NotificationService.kt
│   └── event/                    # Event listeners that trigger application services
│       ├── CoreEventListener.kt
│       └── DeathTimerEventListener.kt
├── domain/                       # Domain layer - business logic and rules
│   ├── core/                     # Core domain
│   │   ├── model/                # Domain models
│   │   └── service/              # Domain services
│   ├── player/                   # Player management domain
│   │   ├── model/
│   │   └── service/
│   │       └── TabListService.kt
│   ├── deathtimer/               # Death timer domain
│   │   ├── model/
│   │   │   └── UserTimeout.kt
│   │   ├── service/
│   │   │   └── DeathTimerService.kt
│   │   └── repository/
│   │       └── UserTimeoutRepository.kt
│   └── notification/             # Notification domain
│       ├── model/
│       │   └── Notification.kt
│       ├── service/
│       │   └── NotificationService.kt
│       └── port/                 # Ports (interfaces) for external services
│           └── NotificationSender.kt
├── infrastructure/               # Infrastructure layer - technical details
│   ├── config/                   # Configuration management
│   │   ├── model/
│   │   │   ├── CoreConfig.kt
│   │   │   └── DeathTimerConfig.kt
│   │   └── repository/
│   │       ├── ConfigRepository.kt
│   │       ├── CoreConfigRepository.kt
│   │       └── DeathTimerConfigRepository.kt
│   ├── persistence/              # Data persistence
│   │   └── file/
│   │       └── FileConfigRepository.kt
│   └── integration/              # External integrations
│       └── discord/              # Discord integration
│           ├── DiscordWebhookClient.kt
│           └── DiscordNotificationSender.kt
└── HuntcraftPlugin.kt            # Main plugin class
```

## Layered Architecture

Each domain will follow a layered architecture:

1. **Domain Layer**
   - Domain Models: Entities, Value Objects, Aggregates
   - Domain Services: Business logic operations
   - Repositories (interfaces): Data access contracts

2. **Application Layer**
   - Application Services: Orchestrate use cases
   - Event Handlers: React to external events

3. **Infrastructure Layer**
   - Repository Implementations: Data access implementations
   - External Service Adapters: Integration with external systems
   - Configuration: System configuration

## Communication Between Domains

Domains will communicate through:

1. **Domain Events**: For loose coupling between domains
2. **Application Services**: For orchestrating cross-domain operations
3. **Shared Kernel**: For concepts shared between domains

## Implementation Plan

### 1. Core Domain

The core domain will contain essential plugin functionality:

- **Models**:
  - `Player`: Representation of a player in the system
  - `ServerInfo`: Information about the server

- **Services**:
  - `CoreService`: Essential operations

### 2. Player Management Domain

This domain will handle player-related functionality:

- **Models**:
  - `PlayerSession`: Information about a player's session

- **Services**:
  - `TabListService`: Manages the player tab list
  - `PlayerSessionService`: Manages player sessions

### 3. Death Timer Domain

This domain will handle player deaths and timeouts:

- **Models**:
  - `UserTimeout`: Information about a player's death timeout
  - `DeathEvent`: Represents a player death event

- **Services**:
  - `DeathTimerService`: Manages death timeouts
  - `TimeoutEnforcementService`: Enforces timeout rules

- **Repositories**:
  - `UserTimeoutRepository`: Stores and retrieves timeout information

### 4. Notification Domain

This domain will handle sending notifications:

- **Models**:
  - `Notification`: Represents a notification
  - `NotificationType`: Types of notifications (Death, Achievement)

- **Services**:
  - `NotificationService`: Creates and processes notifications

- **Ports**:
  - `NotificationSender`: Interface for sending notifications

### 5. Infrastructure Layer

The infrastructure layer will handle technical concerns:

- **Configuration**:
  - `ConfigRepository`: Interface for configuration storage
  - `FileConfigRepository`: File-based implementation

- **Integration**:
  - `DiscordWebhookClient`: Client for Discord webhooks
  - `DiscordNotificationSender`: Implementation of NotificationSender

## Dependency Injection

Replace the singleton pattern with proper dependency injection:

1. Create a simple DI container
2. Register services at plugin startup
3. Inject dependencies through constructors

## Main Plugin Class

The main plugin class will:

1. Initialize the DI container
2. Register services
3. Register event listeners
4. Load configuration

## Benefits of This Architecture

1. **Clear Boundaries**: Each domain has clear responsibilities
2. **Testability**: Dependencies are explicit and can be mocked
3. **Flexibility**: Easy to add or modify features
4. **Maintainability**: Code organization reflects business domains
5. **Scalability**: New domains can be added without affecting existing ones