package de.cloudypanda.main.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Abstract class for handling file management based on given config model
 * @param <T> Config model class
 */
@Getter
public abstract class AbstractFileManager<T> {

    private final Path filePath;
    private final Huntcraft huntcraft;
    private final Class<T> clazz;

    public AbstractFileManager (String fileName, Huntcraft huntcraft, Class<T> clazz){
        this.filePath = Path.of(String.format("%s.json", fileName));
        this.huntcraft = huntcraft;
        this.clazz = clazz;
    }

    /**
     * Create and initialize the config file if it does not exist
     * Also allows to overwrite the {@link AbstractFileManager#afterInit()} method to add custom logic after the file has been created
     */
    public void createFileIfNotExists() {
        if(!checkIfFileExists()){
            createFile();
            this.saveToFile(newClazzInstance());
            afterInit();
        } else {
            huntcraft.getComponentLogger().info("File already exists, continuing");
        }
    }

    /**
     * Override this method to add custom logic after the file has been created
     */
    public void afterInit() {
        // Override this method to add custom logic after the file has been created
    };

    /**
     * Save the config model to the file
     * @param config Config model to save
     */
    public void saveToFile(T config) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(filePath.toFile(), config);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong writing to file. " + e.getMessage());
        }
    }

    /**
     * Read the config model from the file
     * @return Config model
     */
    public T readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(this.getFilePath().toFile(), clazz);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong reading from file. {}", e.getMessage());
        }
        return newClazzInstance();
    }

    /**
     * Create the file
     */
    private void createFile(){
        try{
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if the file exists
     * @return True if the file exists, false otherwise
     */
    private boolean checkIfFileExists(){
        return Files.exists(filePath);
    }

    /**
     * Create a new instance of the config model
     * @return New instance of the config model
     */
    private T newClazzInstance() {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
