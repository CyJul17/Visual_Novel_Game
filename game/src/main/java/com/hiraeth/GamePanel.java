package com.hiraeth;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.List;
import javax.sound.sampled.*;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiraeth.Data_Model.Dialogue;


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
    private Timer typewriter;
    private int charIndex = 0;
    private String fullText;
    private JLabel backgroundLabel;
    private JLabel characterLabel;
    private JTextArea dialogBox;
    private List<Dialogue> script;
    private int currentLine = 0;
    private float charOpacity = 0.0f;
    private float bgOpacity = 0.0f;
    private Timer charFadeTimer;
    private Timer bgFadeTimer;
    private Image currentCharImage;
    private Image currentBgImage;
    private String lastBackground = "";
    private String lastCharacter = "";
    private Clip bgm; // The banger :3
    private SettingPanel settings;
    private int textSpeed = 30;

    // ######################################## Constructor ###################################################

    public GamePanel() {

        loadScript("intro.json");
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

        this.add(dialogBox);

        //adding seetings
        settings = new SettingPanel(this);
        this.add(settings);
        this.setComponentZOrder(settings, 0);


        updateVisuals();

        
        if (script != null && !script.isEmpty()) {

            Dialogue firstLine = script.get(0); 
            String text = dialogueFormat(firstLine.name, firstLine.text);
            typeWriterEffect(text, false);
        }

        //Mouse Eventlistener
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                continueClick();
            }
        });

        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "continue");
        this.getActionMap().put("continue", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                continueClick();
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
    public void launchGame() {

        playBGM("Far Away(intro).wav");

        if(currentLine == 0 && script != null && !script.isEmpty()) {

            Dialogue firstLine = script.get(0);
            String initialText = dialogueFormat(firstLine.name, firstLine.text);
            typeWriterEffect(initialText, false);
            updateVisuals();
        }
    }

    public void playBGM(String soundFile) {

        if (bgm != null && bgm.isRunning()) {

            bgm.stop();
            bgm.close();
        }

        try {

            InputStream is = getClass().getResourceAsStream("/BGM/" +  soundFile);
            if (is == null) return;

            AudioInputStream sourceStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            AudioFormat baseFormat = sourceStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(), 16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(), false
            );

            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);

            bgm = AudioSystem.getClip();
            bgm.open(decodedStream);
            FloatControl gainControl = (FloatControl) bgm.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);


            bgm.loop(Clip.LOOP_CONTINUOUSLY);
            bgm.start();
        } catch (Exception e ) {

            System.out.println("Error cannot acces the mp3..." + e.getMessage());
        }
    }

    public void updateMusic(String fileName) {

        switch (fileName) {

            case "intro.json":

                playBGM("Far Away(intro).wav");
                break;
            case "Fifteen_Years_Later.json":

                playBGM("Relax(kitchen).wav");
                break;
            default:

                System.out.println("There is no music for: " + fileName);
                break;
        }
    }

    public void setTextSpeed(int speed) {
        this.textSpeed = speed;
    }

    // The private ( ͡° ͜ʖ ͡°)
    private String dialogueFormat(String name, String text) {
        
        if (name == null || name.trim().isEmpty()) {

            return text;
        }
        return name + ": " + text;
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

    private void updateVisuals() {

        if (script != null && currentLine < script.size()) {
            
            Dialogue current = script.get(currentLine);
            
            //update Sprite
            if (current.image != null && !current.image.isEmpty()) {
                
                String newCharImg = current.image;
                if (newCharImg != null) {
                    
                    if (!newCharImg.equals(lastCharacter)) {
                        
                        try {
            
                           java.net.URL imgURL = getClass().getResource("/characters/" + current.image);
            
                            if (imgURL != null) {
            
                                System.out.println("Character image found: " + current.image);
                                ImageIcon characterIcon = new ImageIcon(imgURL);
                                Image scaledImage = characterIcon.getImage().getScaledInstance(
                                    450,
                                    550,
                                    Image.SCALE_SMOOTH
                                );
                                
                               currentCharImage = scaledImage;
                               startFadeIn(true);
                               lastCharacter = newCharImg;
                               characterLabel.setVisible(true);
                            } else {
            
                                System.out.println("Error: Character image not found: " + current.image);
                                characterLabel.setIcon(null); 
                            }
                        } catch (Exception e) {
            
                            System.out.println("Error loading character image: " + current.image);
                            e.printStackTrace();
                        }
            
                    } 
                }
            } else {
                if (currentCharImage != null) {
                    startFadeOut(true);
                    lastCharacter = "";
                }
                }
        
            //update Background
        if (current.background != null && !current.background.isEmpty()) {

            String newBgImg = current.background;
             if (!newBgImg.equals(lastBackground)) {

                 try {
                        
                    InputStream bgInputStream = getClass().getResourceAsStream("/backgrounds/" + newBgImg);
                    System.out.println("DEBUG: Looking for: /backgrounds/" + current.background);
                    System.out.println("DEBUG: InputStream found: " + (bgInputStream != null));
                        
                    if (bgInputStream != null) {
        
                        backgroundLabel.setIcon(null);
                        System.out.println("Switching background to: " + current.background);
        
                        // Use BufferedImage for better loading control
                        BufferedImage bufferedImage = ImageIO.read(bgInputStream);
                            
                        if (bufferedImage == null) {
        
                        System.out.println("ERROR: ImageIO.read returned null - file may be corrupted or unsupported format");
                        } else {
        
                        System.out.println("DEBUG: Image width =" + bufferedImage.getWidth() + ", height =" + bufferedImage.getHeight());
                                
                        currentBgImage = bufferedImage.getScaledInstance(
                            backgroundLabel.getWidth(), 
                            backgroundLabel.getHeight(),
                            Image.SCALE_SMOOTH
                        );
        
                        lastBackground = newBgImg;
                        bgOpacity = 0.0f;
                        startFadeIn(false);
    
                        backgroundLabel.setBounds(0, 0, 800, 600);
                        backgroundLabel.revalidate();
                        backgroundLabel.repaint();
                        System.out.println("DEBUG: Background icon set successfully");
                        }
                    } else {
                            
                        System.out.println("Error: Background image not found: " + current.background);
                    }
                } catch (Exception e) {
        
                    System.out.println("Error loading background image: " + current.background);
                    e.printStackTrace();
                }

            } 
        }  else {

                if (currentBgImage != null) {
                    
                startFadeOut(false);
                lastBackground = "";
                }
            }
    }
}
    
    private void startFadeIn(boolean isCharacter) {

        if (isCharacter) {

            if (charFadeTimer != null) charFadeTimer.stop();
            charOpacity = 0.0f;
            charFadeTimer = new Timer(50, e -> {

                charOpacity += 0.05f;
                if (charOpacity >= 1.0f) {

                    charOpacity = 1.0f;
                    charFadeTimer.stop();
                }
                repaint();
            });
            charFadeTimer.start();
        } else {

            if (bgFadeTimer != null) bgFadeTimer.stop();

            bgOpacity = 0.0f;
            bgFadeTimer = new Timer(50 , e -> {

                bgOpacity += 0.05f;
                if (bgOpacity >= 1.0f) {

                    bgOpacity = 1.0f;
                    bgFadeTimer.stop();
                }
                repaint();
            });
            bgFadeTimer.start();
        }
    }
    
    private void startFadeOut(boolean isCharacter) {

    if (isCharacter) {

            if (charFadeTimer != null) charFadeTimer.stop();
            charFadeTimer = new Timer(50, e -> {

                charOpacity -= 0.05f;
                if (charOpacity <= 0.0f) {

                    charOpacity = 0.0f;
                    charFadeTimer.stop();
                }
                repaint();
            });
            charFadeTimer.start();
        } else {

            if (bgFadeTimer != null) bgFadeTimer.stop();

            bgFadeTimer = new Timer(50 , e -> {

                bgOpacity -= 0.05f;
                if (bgOpacity <= 0.0f) {

                    bgOpacity = 0.0f;
                    bgFadeTimer.stop();
                }
                repaint();
            });
            bgFadeTimer.start();
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

           updateMusic(fileName);

            //Debugging output
            System.out.println("Script loaded successfully! Total Lines:" + fileName + " -> " + script.size());

        } catch (Exception e) {

            System.out.println("Error loading script: Check if your JSON fields match your Dialogue class fields.");
            e.printStackTrace();
            this.script = new java.util.ArrayList<>(); // Becomes empty script to prevent null pointer.
        }
    }

    private void typeWriterEffect(String text, boolean isItalic) {

        if (typewriter != null && typewriter.isRunning()) {

            typewriter.stop();
        }

        int style = isItalic ? Font.ITALIC : Font.PLAIN;
        dialogBox.setFont(new Font("Arial", style, 23));

        charIndex = 0;
        fullText = text;
        dialogBox.setText(""); // Clear the dialog box
        typewriter = new Timer(this.textSpeed, e -> {

            if (charIndex < fullText.length()) {

                dialogBox.append(String.valueOf(fullText.charAt(charIndex))); 
                charIndex++;
                } else {

                    typewriter.stop();
                }
        });
        typewriter.setInitialDelay(400);
        typewriter.start();
    }
    private void playSound(String soundFile) {

        try {

            InputStream is = getClass().getResourceAsStream("/Sound_Effects/" + soundFile);
            if(is == null) {

                System.out.println("The file is Null");
            }

            InputStream bufferedIn = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();

        } catch (Exception e) {

            System.out.println("Cannot access the sound file...");
            e.printStackTrace();
        }
    }
    private void continueClick() { 

        if (script == null || script.isEmpty()) return;
        
        if (typewriter != null && typewriter.isRunning()) {

            typewriter.stop();
            dialogBox.setText(fullText);
            return;
        }
       
        if (script.isEmpty()) {

            System.out.println("Script not loaded or empty.");
            return;
        }

        if (currentLine < script.size() - 1) {

            currentLine++;
            
            Dialogue dialogue = script.get(currentLine);
            if ("choice".equals(dialogue.type)) {
                
                buttonOptions(dialogue.options);
                return;
            }
            
            playSound("Turning_pages.wav");
            //ask the name if it is line 2.
            if (currentLine == 2 && !nameAsk) {

                askForName();
                nameAsk = true;
            }
            
            Dialogue forTheButler = script.get(currentLine);
            if (forTheButler != null && forTheButler.name != null && forTheButler.name.trim().equalsIgnoreCase("Butler")) {

                playBGM("Myuu-Edge-of-Life(butler).wav");
            }

            if (currentLine >= script.size() - 1) {

                JOptionPane.showMessageDialog(this, "Saving your progress...");
                return;
            }



            String displayName = dialogue.name != null ? dialogue.name.trim() : ""; // Get character name from JSON
            String rawText = dialogue.text != null ? dialogue.text : "";
            boolean isInternal = false;
            
            // swap the name of the main character to the input
            if ("MainChar (Internal)".equalsIgnoreCase(displayName)) {

                isInternal = true;
                displayName = playerName + " (Internal)";
            } else if ("MainChar".equalsIgnoreCase(displayName)) {

                displayName = playerName;
            }

            // change the (input name in the JSON file
            if (rawText.contains("(input name)")) {

                rawText = rawText.replace("(input name)", playerName);
            } else if (rawText.contains("(Player Name)")) {
                
                rawText = rawText.replace("(Player Name)", playerName);
            } else if ((rawText.contains("[Player Name]"))) { 

                rawText = rawText.replace("[Player Name]", playerName);
            }

            // display it in the dialog box
            String finalText;
            if (displayName != null && !displayName.trim().isEmpty()) {

                finalText = dialogueFormat(displayName, rawText);
            } else {

                finalText = rawText;
            }

            updateVisuals();
            typeWriterEffect(finalText, isInternal);
           

            

            backgroundLabel.repaint();
            characterLabel.repaint();


            this.revalidate();
            this.repaint();
        } else {

            System.out.println("Switching file...");
            nextSlide("Fifteen_Years_Later.json");
        }
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
        updateVisuals();
        typeWriterEffect(text, false);
    }



    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        //Draw background opacity.
         if (currentBgImage != null) {

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, bgOpacity));
            g2d.drawImage(currentBgImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        // Draw Character opacity.
        if (currentCharImage != null) {
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, charOpacity));
            g2d.drawImage(currentCharImage, 175, 50, 450, 550, null);
        }

        g2d.dispose(); // clean the graphics context to prevent memo leaks.
    }


    private void nextSlide(String nextFile) {

        if (script == null) return;

        if (currentLine >= script.size()) return;

        Dialogue current = script.get(currentLine);

        if ("choice".equals(current.type)) {

            buttonOptions(current.options);
        } else {

            typeWriterEffect(current.text, false);
        }

        loadScript(nextFile);
        currentLine = 0;
        lastBackground = "";
        lastCharacter = "";

        if (!script.isEmpty()) {
            
            Dialogue firstLine = script.get(0);
            updateVisuals();
            String displayName = firstLine.name != null ? firstLine.name : "";
            String text = dialogueFormat( displayName, firstLine.text);
            typeWriterEffect(text, false);
        }

       this.setComponentZOrder(dialogBox, 0);
       this.repaint();
    }

} // this is the end of the class (If ever you delete this you gonna mess up the code ToT).