package com.hiraeth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hiraeth.Data_Model.Dialogue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


/*
*  This is the main panel of the visual novel.
*  It's purpose is to layout the backgrounds, characters, and dialog box.
*  Furthermore, it is also used to load and update the script, that is stored in a JSON file.
*  it also listen for mouse clicks and keyboard types.
*  Note: System.out.println() are used for debugging.
*/


public class GamePanel extends JPanel {

    private String playerName = "Player";
    private Timer typewriter;
    private int charIndex = 0;
    private String fullText;
    private JLabel backgroundLabel;
    private JLabel characterLabel;
    private JTextArea dialogBox;
    private List<Dialogue> script;
    private int currentLine = 0;

    // ######################################## Constructor ###################################################

    public GamePanel() {

        loadScript();
        this.setLayout(null);
        this.setOpaque(false);

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
        this.add(characterLabel);
        this.add(backgroundLabel);

        updateVisuals();

        
        if (script != null && !script.isEmpty()) {
            Dialogue firstLine = script.get(0); 
            String initialText = firstLine.name + " : " + firstLine.text;
            typeWriterEffect(initialText, false);
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
        this.setComponentZOrder(backgroundLabel, this.getComponentCount() - 1);
        this.repaint(); // Force a repaint to ensure the new Z-order is applied
       
        this.setComponentZOrder(dialogBox,0);
        this.setComponentZOrder(characterLabel,1);
        this.setComponentZOrder(backgroundLabel,2);
    }

    // ######################################### Methods ###################################################

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
                    
                    characterLabel.setIcon( new ImageIcon(scaledImage));
                    characterLabel.setVisible(true);
                } else {

                    System.out.println("Error: Character image not found: " + current.image);
                    characterLabel.setIcon(null); 
                }
            } catch (Exception e) {

                System.out.println("Error loading character image: " + current.image);
                e.printStackTrace();
            }

        } else {

            characterLabel.setIcon(null);
            characterLabel.revalidate();
        }
        
            //update Background
        if (current.background != null && !current.background.isEmpty()) {

            try {
                
               InputStream bgInputStream = getClass().getResourceAsStream("/backgrounds/" + current.background);
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
                        
                        Image scaledImage = bufferedImage.getScaledInstance(
                           backgroundLabel.getWidth(), 
                           backgroundLabel.getHeight(),
                           Image.SCALE_SMOOTH
                        );

                        backgroundLabel.setIcon(new ImageIcon(scaledImage));
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
    }
}
    
    

    private void loadScript() {

        ObjectMapper mapper = new ObjectMapper(); 
        try {

            InputStream is = getClass().getResourceAsStream("/intro.json");
            if (is == null) {

                System.out.println(("Error: We couldn't find the script.json in resources."));
                return;
            }
            script = mapper.readValue(is, new TypeReference<List<Dialogue>>() {});

            //Debugging output
            System.out.println("List size: " + script.size());
            System.out.println("First line: " + script.get(0).name + ":" + script.get(0).text);
            System.out.println("Script loaded successfully! Total Lines:" + script.size());

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
        typewriter = new Timer(30, e -> {

            if (charIndex < fullText.length()) {

                dialogBox.append(String.valueOf(fullText.charAt(charIndex))); 
                charIndex++;
                } else {

                    typewriter.stop();
                }
        });
        typewriter.start();
    }

    private void continueClick() { 

        if (script.isEmpty()) {

            System.out.println("Script not loaded or empty.");
            return;
        }

        if (typewriter != null && typewriter.isRunning()) {

            typewriter.stop();
            dialogBox.setText(fullText);
            return;
        }


        if (currentLine < script.size() - 1) {

            currentLine++;


            //ask the name if it is line 2.
            if (currentLine == 2) {

                askForName();
            }

            if (currentLine >= script.size()) {

                System.out.println("End of the script reached.");
                return;
            }


            Dialogue dialogue = script.get(currentLine);
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
            }

            // display it in the dialog box
            String finalText;
            if (displayName != null && !displayName.trim().isEmpty()) {

                finalText = displayName + " : " + rawText;
            } else {

                finalText = rawText;
            }

            typeWriterEffect(finalText, isInternal);
           

            updateVisuals();

            backgroundLabel.repaint();
            characterLabel.repaint();

            this.revalidate();
            this.repaint();
        }
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
    }
}