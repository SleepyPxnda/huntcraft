package de.cloudypanda.main.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.util.ConfigModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
public class FileManager {
    private final Path filePath;
    private final Huntcraft huntcraft;

    public FileManager (String fileName, Huntcraft huntcraft){
        this.filePath = Path.of(String.format("%s.json", fileName));
        this.huntcraft = huntcraft;
    }

    public void createFileIfExists() {
        if(!checkIfFileExists()){
            createFile();
            saveToFile(new ConfigModel());
        } else {
            huntcraft.getComponentLogger().info("File already exists, continuing");
        }
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


    public void saveToFile(ConfigModel config) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(filePath.toFile(), config);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong writing to file. " + e.getMessage());
        }
    }

    public ConfigModel readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        ConfigModel model = null;
        try {
            model =  mapper.readValue(filePath.toFile(), ConfigModel.class);
        } catch (IOException e) {
            huntcraft.getComponentLogger().error("Something went wrong reading from file. " + e.getMessage());
        }
        return model;
    }
}
