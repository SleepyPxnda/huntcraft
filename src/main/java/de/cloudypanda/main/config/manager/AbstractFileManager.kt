package de.cloudypanda.main.config.manager

import com.fasterxml.jackson.databind.ObjectMapper
import de.cloudypanda.main.Huntcraft
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

/**
 * Abstract class for handling file management based on given config model
 * @param <T> Config model class
 */
abstract class AbstractFileManager<T>(private val fileName: String, val huntcraft: Huntcraft, private val clazz: Class<T>) {

    private val filePath: Path = Path.of("$fileName.json")

    /**
     * Create and initialize the config file if it does not exist
     * Also allows to overwrite the {@link AbstractFileManager#afterInit()} method to add custom logic after the file has been created
     */
    fun createFileIfNotExists(): T {
        if (!checkIfFileExists()) {
            createFile()
            this.saveToFile(newClazzInstance())
            afterInit()
        } else {
            huntcraft.componentLogger.info("File $fileName already exists")
        }

        return readFromFile()
    }

    /**
     * Override this method to add custom logic after the file has been created
     */
    open fun afterInit() {
        // Override this method to add custom logic after the file has been created
    }

    /**
     * Save the config model to the file
     * @param config Config model to save
     */
    fun saveToFile(config: T) {
        val mapper = ObjectMapper()

        try {
            mapper.writeValue(filePath.toFile(), config)
        } catch (e: IOException) {
            huntcraft.componentLogger.error("Something went wrong writing to file. " + e.message)
        }
    }

    /**
     * Read the config model from the file
     * @return Config model
     */
    fun readFromFile(): T {
        val mapper = ObjectMapper()
        try {
            return mapper.readValue(filePath.toFile(), clazz)
        } catch (e: IOException) {
            huntcraft.componentLogger.error("Something went wrong reading from file. {}", e.message)
        }
        return newClazzInstance()
    }

    /**
     * Create the file
     */
    private fun createFile() {
        try {
            Files.createFile(filePath)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Check if the file exists
     * @return True if the file exists, false otherwise
     */
    private fun checkIfFileExists(): Boolean {
        return Files.exists(filePath)
    }

    /**
     * Create a new instance of the config model
     * @return New instance of the config model
     */
    private fun newClazzInstance(): T {
        try {
            val constructor = clazz.getDeclaredConstructor()
            return constructor.newInstance()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}