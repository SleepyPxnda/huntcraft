package de.cloudypanda.main.file;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.cloudypanda.main.Huntcraft;
import de.cloudypanda.main.util.ConfigModel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
public class FileManager {
    private final Path filePath;

    public FileManager (String fileName){
        this.filePath = Path.of(String.format("%s.json", fileName));
    }

    public void createFileIfExists() {
        if(!checkIfFileExists()){
            createFile();
            saveToFile(new ConfigModel());
        } else {
            System.out.println("File already exists, continuing");
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
           System.out.println("Something went wrong writing to file. " + e.getMessage());
        }
    }

    public ConfigModel readFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        ConfigModel model = null;
        try {
            model =  mapper.readValue(filePath.toFile(), ConfigModel.class);
        } catch (IOException e) {
            System.out.println("Something went wrong reading from file. " + e.getMessage());
        }
        return model;
    }
}
