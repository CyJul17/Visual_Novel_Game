package com.hiraeth;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import com.hiraeth.Data_Model.Dialogue;


public class VisualManager {

    private JPanel panel;
    private JLabel characterLabel, backgroundLabel;
    public Image currentCharImage, currentBgImage;
    public float charOpacity = 0.0f, bgOpacity = 0.0f;
    private Timer charFade, bgFade;
    private String lastCharacter = "", lastBackground = "";

    public VisualManager(JPanel panel, JLabel charLabel, JLabel bgLabel) {

        this.panel = panel;
        this.characterLabel = charLabel;
        this.backgroundLabel = bgLabel;
    }
//  methods
    public void update(Dialogue dialogue) {

        handleSprite(dialogue.image);
        handleBackground(dialogue.background);
    }

    private void handleSprite(String sprite) {

        if (sprite != null && !sprite.isEmpty()) {

            if (!sprite.equals(lastCharacter)) {
                try {
                    URL imgURL = getClass().getResource("/characters/" + sprite);
                    if (imgURL != null) {

                        ImageIcon icon = new ImageIcon(imgURL);
                        currentCharImage = icon.getImage().getScaledInstance(450, 550, Image.SCALE_SMOOTH);
                        startFade(true, true);
                        lastCharacter = sprite;
                    }
                } catch (Exception e) { e.printStackTrace(); }
            } else {
                
                if (currentCharImage != null) {

                    startFade(true, false);
                    lastCharacter = "";
                }
            }   
        }
    }

    private void handleBackground(String bGround) {
    
         if (bGround != null && !bGround.isEmpty()) {

            if (!bGround.equals(lastBackground)) {

                try {
                   InputStream is = getClass().getResourceAsStream("/backgrounds/" + bGround);
                    if (is != null) {

                        BufferedImage buffer = ImageIO.read(is);
                        currentBgImage = buffer.getScaledInstance(backgroundLabel.getWidth(), backgroundLabel.getHeight(), Image.SCALE_SMOOTH);
                        startFade(false, true);
                        lastBackground = bGround;
                    }
                } catch (Exception e) { e.printStackTrace(); }
            } 
        }
    }


    private void startFade(boolean isCharacter, boolean fadeIn) {

        Timer time = new Timer (50, e -> {

            if (isCharacter) {

                charOpacity += fadeIn ? 0.05f : -0.05f;
                if (charOpacity >= 1.0f || charOpacity <= 0.0f) {
                    
                    
                    charOpacity = Math.max(0, Math.min(1, charOpacity));
                    ((Timer)e.getSource()).stop();
                }
                
            } else {
                
                bgOpacity += fadeIn ? 0.05f : -0.05f;
                if (bgOpacity >= 1.0f || bgOpacity <= 0.0f) {
                    
                    
                    bgOpacity = Math.max(0, Math.min(1, bgOpacity));
                    ((Timer)e.getSource()).stop();
                }
            }
            panel.repaint();
        });
        time.start();
    }

    public void resetTracker() {

       lastCharacter = "";
       lastBackground = "";
    }
}
