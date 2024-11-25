# Huntcraft

Huntcraft is a Minecraft plugin that introduces various features such as death timers, advent calendar challenges, and
more. This project is built using Java and Gradle.

## Features

- **Death Timer**: Players are temporarily banned from rejoining the server after death.
- **Advent Calendar**: Daily challenges for players to complete.

## Requirements

- Java 8 or higher
- Gradle
- Minecraft server

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/SleepyPxnda/huntcraft.git
    cd huntcraft
    ```

2. Build the project using Gradle:
    ```sh
    ./gradlew build
    ```

3. Copy the generated JAR file from `build/libs` to your Minecraft server's `plugins` directory.

4. Start your Minecraft server.

## Configuration

### Death Timer

The death timer configuration is managed through a JSON file. The file is automatically created if it does not exist.
You can modify the settings in this file to adjust the death timeout duration and other related settings.

### Advent Calendar

The advent calendar configuration is also managed through a JSON file. This file contains the daily challenges for
players. You can add or modify challenges in this file.

## Usage

### Death Timer

When a player dies, they are temporarily banned from rejoining the server. The duration of the ban is configurable.
Players will receive a message indicating when they can rejoin.

### Advent Calendar

Players will receive a daily challenge message when they join the server. The challenges are configured in the advent
calendar JSON file.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Contact

For any questions or suggestions, please open an issue or contact the project maintainer.