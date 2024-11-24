package de.cloudypanda.main.common.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.core.config.CoreConfigModel;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public void createFileIfNotExists() {
        if(!checkIfFileExists()){
            createFile();
            this.saveToFile(newClazzInstance());
            afterInit();
        } else {
            huntcraft.getComponentLogger().info("File already exists, continuing");
        }
    }

    public void afterInit() {
        // Override this method to add custom logic after the file has been created
    };

    public void saveToFile(T config) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(filePath.toFile(), config);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong writing to file. " + e.getMessage());
        }
    }

    public T readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(this.getFilePath().toFile(), clazz);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong reading from file. {}", e.getMessage());
        }
        return newClazzInstance();
    }

    private void createFile(){
        try{
            Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfFileExists(){
        return Files.exists(filePath);
    }

    private T newClazzInstance() {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
