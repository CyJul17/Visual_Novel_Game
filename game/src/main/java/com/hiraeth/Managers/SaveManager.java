package com.hiraeth.Managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiraeth.Data_Model.SaveData;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class SaveManager {

    private static final String SAVE_FILE = "Save_Files/Save.json";
    private final ObjectMapper mapper;

    public SaveManager() {

        this.mapper = new ObjectMapper();
    }

    //Saving method

    public void save(String scriptName, String playerName, int currentLine) {
        
        
        try { 
            
            File file = new File(SAVE_FILE);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {

                parent.mkdirs();
            }
            SaveData data = new SaveData(scriptName, playerName, currentLine);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            System.out.println("Saving: " + scriptName + " at line " + currentLine);
            mapper.writeValue(new File(SAVE_FILE), data);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while saving the game...", "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public SaveData load() {

        try {

            File file = new File(SAVE_FILE);

            if (file.exists()) {

                return mapper.readValue(file, SaveData.class);
            }
        } catch (Exception e) {

             JOptionPane.showMessageDialog(null, "Error while loading the game...", "Error",JOptionPane.ERROR_MESSAGE);
        }
        return null; // if no save return null.
    }
}