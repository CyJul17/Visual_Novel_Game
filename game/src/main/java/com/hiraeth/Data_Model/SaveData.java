package com.hiraeth.Data_Model;

public class SaveData {

    public String currentScript;
    public String playerName;
    public int currentLine;

    public SaveData() {}
    
    public SaveData(String script, String name, int line) {
    
        this.currentScript = script;
        this.playerName = name;
        this.currentLine = line;
    }
}