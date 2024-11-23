package de.cloudypanda.main.common.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public abstract class AbstractFileManager<T> {

    private final Path filePath;
    private final Huntcraft huntcraft;

    public AbstractFileManager (String fileName, Huntcraft huntcraft){
        this.filePath = Path.of(String.format("%s.json", fileName));
        this.huntcraft = huntcraft;
    }

    public void createFileIfNotExists() {
        if(!checkIfFileExists()){
            createFile();
        } else {
            huntcraft.getComponentLogger().info("File already exists, continuing");
        }
    }

    public void saveToFile(T config) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(filePath.toFile(), config);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong writing to file. " + e.getMessage());
        }
    }

    public abstract T readFromFile();

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
}
