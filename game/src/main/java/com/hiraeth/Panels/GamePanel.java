package com.hiraeth.Panels;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.InputStream;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiraeth.Data_Model.Dialogue;
import com.hiraeth.Data_Model.SaveData;
import com.hiraeth.Managers.MusicManager;
import com.hiraeth.Managers.SaveManager;
import com.hiraeth.Managers.TypingManager;
import com.hiraeth.Managers.VisualManager;


/*
*  This is the main panel of the visual novel.
*  It's purpose is to layout the backgrounds, characters, and dialog box.
*  Furthermore, it is also used to load and update the script, that is stored in a JSON file.
*  it also listen for mouse clicks and keyboard types.
*  Note: System.out.println() are used for debugging.
*/


public class GamePanel extends JPanel {

    private String playerName = "Player";
    private boolean nameAsk = false;
    private JLabel characterLabel;
    private JLabel backgroundLabel;
    private JTextArea dialogBox;
    private List<Dialogue> script;
    private  int currentLine = 0;
    private SettingPanel settings;
    private VisualManager visualMan;
    private TypingManager typingMan;
    private SaveManager saveManager = new SaveManager();
    private MusicManager musicMan = new MusicManager();
    private String currentScriptLine = "intro.json";
    // ######################################## Constructor ###################################################

    public GamePanel() {

        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        
        
        //dialog box
        dialogBox = new JTextArea();
        dialogBox.setBounds(50, 450, 700, 100);
        dialogBox.setEditable(false);
        dialogBox.setFont(new Font("Arial", Font.PLAIN, 23));
        dialogBox.setOpaque(true);
        dialogBox.setBackground(new java.awt.Color(255, 255, 255, 200));
        dialogBox.setLineWrap(true);
        dialogBox.setWrapStyleWord(true);
        
        //character sprite
        characterLabel = new JLabel();
        characterLabel.setOpaque(false);
        characterLabel.setBounds(175, 50, 450, 550);
        
        //background
        backgroundLabel = new JLabel();
        backgroundLabel.setSize(800, 600);
        backgroundLabel.setBounds(0, 0, 800, 600);

        //Instantation 
         visualMan = new VisualManager(this, characterLabel, backgroundLabel);
         typingMan = new TypingManager(dialogBox, this);
         this.add(dialogBox);
         
         //adding seetings
         settings = new SettingPanel(this);
         this.add(settings);
         this.setComponentZOrder(settings, 0);
         

        
        if (script != null && !script.isEmpty()) {

            Dialogue firstLine = script.get(0); 
            String text = dialogueFormat(firstLine.name, firstLine.text);
            
            typingMan.typeText(text);
        }

        //Mouse Eventlistener
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                typingMan.continueType();
            }
        });
        // Keyboard adpater 
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "continue");
        this.getActionMap().put("continue", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                typingMan.continueType();
            }
        });
        
        // To make the background is at the back.
        this.repaint(); // Force a repaint to ensure the new Z-order is applied
        this.setComponentZOrder(backgroundLabel, this.getComponentCount() - 1);
        
        this.setComponentZOrder(dialogBox,0);
        this.setComponentZOrder(characterLabel,1);
        this.setComponentZOrder(backgroundLabel,2);
    }

    // ######################################### Methods ###################################################

    // The public 

    public void startNewGame() {

        this.playerName = "Player";
        this.currentLine = 0;
        this.nameAsk = false;
        loadScript("intro.json");
        updateContent();
    }

    public void continueGame() {

        SaveData data = saveManager.load();
        if (data != null) {

            this.playerName = data.playerName;
            this.currentLine = data.currentLine;
            loadScript(data.currentScript);
            updateContent();
            musicMan.updateMusic(data.currentScript);
        } else {

            JOptionPane.showMessageDialog(this, "No save data found. :(");
            startNewGame();
        }
    }
    public void advanceDialogue() {

        currentLine++;
        if (currentLine < script.size()) {
            saveManager.save(currentScriptLine, playerName, currentLine);

            updateContent();
             musicMan.playSound("Turning_pages.wav");

        } else {

            nextSlide("Fifteen_Years_Later.json");
        }
    }

    public void setTextSpeed(int speed) {

        typingMan.setTextSpeed(speed);
    }
        
    public void launchGame() {

        musicMan.playBGM("Far Away(intro).wav");

        if(currentLine == 0 && script != null && !script.isEmpty()) {

            Dialogue firstLine = script.get(0);
            String initialText = dialogueFormat(firstLine.name, firstLine.text);
            typingMan.typeText(initialText);
            updateContent();
        }
    }

    public void resumeGame() {

        SaveData data = saveManager.load();

        if (data != null) {

            this.playerName = data.playerName;
            this.currentLine = data.currentLine;
            loadScript(data.currentScript);
            JOptionPane.showMessageDialog(this, "Resume for save: " + data.currentScript + " at line " + currentLine);
        } else {
            
            JOptionPane.showMessageDialog(this, "No save file found.");
            loadScript("intro.json");
        }
    }
  
    // The private ( ͡° ͜ʖ ͡°)

    private String mainCharacterName(String name) {

        if(name == null || name.trim().isEmpty()) return "";

        String processedName = "";
        switch (name) {
            case "MainChar":
                processedName = playerName + ": ";
                break;
            case "MainChar (Internal)":
                processedName = playerName + " (Internal): ";
                break;
            default:
                processedName = name.replace("[Player]", playerName) + ": ";
                break;
        }
        return processedName;

    }
    private String dialogueFormat(String name, String text) {
        
        String displayName = mainCharacterName(name);
        String processedText = (text != null) ? text : "";
        if (text != null && text.contains("[Player Name]")) {

            processedText = text.replace("[Player Name]", playerName);
        }
        return displayName + processedText;   
    }

    public void toggleSettings() {

        settings.setVisible(!settings.isVisible());

        if (settings.isVisible()) {

            this.setComponentZOrder(settings, 0);
        }
        this.repaint();
    }

    private void askForName() {

        String input = JOptionPane.showInputDialog(this, " The director named you, what is it?  ", "Enter your name here.");
        if (input != null && !input.trim().isEmpty()) {

            playerName = input.trim();
        }
    }

    private void updateContent() {

        if (script == null || currentLine >= script.size()) return;
        Dialogue current = script.get(currentLine);

        if ("input".equals(current.type) && !nameAsk) {

            askForName();
            nameAsk = true;
        }
           
            visualMan.update(current);

        if (currentLine == 28 && current.name.equals("Butler")) {

            musicMan.playBGM("Myuu-Edge-of-Life(butler).wav");
        }

        String formattedText = dialogueFormat(current.name, current.text);
        typingMan.typeText(formattedText);

        if ("choice".equals(current.type) && current.options != null) {

            buttonOptions(current.options);
        }
        
    }   
    

    private void loadScript(String fileName) {


        ObjectMapper mapper = new ObjectMapper(); 
        try {

            InputStream is = getClass().getResourceAsStream("/" + fileName);
            if (is == null) {

                System.out.println(("Error: We couldn't find the " + fileName + " in resources."));
                return;
            }
            script = mapper.readValue(is, new TypeReference<List<Dialogue>>() {});

           musicMan.updateMusic(fileName);

            //Debugging output
            System.out.println("Script loaded successfully! Total Lines:" + fileName + " -> " + script.size());

        } catch (Exception e) {

            System.out.println("Error loading script: Check if your JSON fields match your Dialogue class fields.");
            e.printStackTrace();
            this.script = new java.util.ArrayList<>(); // Becomes empty script to prevent null pointer.
        }

        this.currentScriptLine = fileName;
       // saveManager.save(currentScriptLine, playerName, currentLine);
    }

    private void buttonOptions(List<Dialogue.Option> options) {
        if (options == null) return;
        

        for (int i = 0; i < options.size(); i++) {
            Dialogue.Option choices = options.get(i);
            JButton choiceButton = new JButton(choices.text);
            choiceButton.setBounds(200, 200 + (i * 80), 400, 50);
            choiceButton.addActionListener(e -> {

                handleChoice(choices.target);

                Container parent = choiceButton.getParent();
                if (parent != null) {

                    for (Component c : parent.getComponents()) {
                        if (c instanceof JButton) {
                            parent.remove(c);
                        }
                    }
                    parent.revalidate();
                    parent.repaint();
                }
            });
            this.add(choiceButton);
        }
        this.setComponentZOrder(dialogBox, 0);
        this.revalidate();
        this.repaint();
}

    private void handleChoice(String targetPath) {

        dialogBox.setText("");

        String finalPath = targetPath.endsWith(".json") ? targetPath : targetPath + ".json";
        loadScript(finalPath);
      

        currentLine = 0;
        Dialogue firstLine = script.get(0);
        String text = dialogueFormat(firstLine.name, firstLine.text);
        typingMan.typeText(text);
        updateContent();
    }



    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        //Draw background opacity.
         if (visualMan.currentBgImage != null) {

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visualMan.bgOpacity));
            g2d.drawImage(visualMan.currentBgImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        // Draw Character opacity.
        if (visualMan.currentCharImage != null) {
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, visualMan.charOpacity));
            g2d.drawImage(visualMan.currentCharImage, 175, 50, 450, 550, null);
        }

        g2d.dispose(); // clean the graphics context to prevent memo leaks.
    }


    private void nextSlide(String nextFile) {

     if (script == null) return;

     this.currentLine = 0;
     
     loadScript(nextFile);
     this.currentScriptLine = nextFile;
     visualMan.resetTracker();
     if (script != null && !script.isEmpty()) {

         updateContent();
     }
    }

} // this is the end of the class (If ever you delete this you gonna mess up the code ToT).