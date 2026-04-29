package com.hiraeth.Data_Model;
import java.util.List;
import org.json.JSONArray;


public class Dialogue {

    public String name;
    public String text;
    public String background;
    public String image; 
    public String type;
    public List<Option> options;

    public Dialogue() {}

    //Contructor for dialogue w/o choices.
    public Dialogue(String name, String text, String background, String image, String type) {
        
        this.name = name;
        this.text = text;
        this.background = background;
        this.image = image;
        this.type = "dialogue";
    }

    public static class Option {
        public String text;
        public String target;
    }
}