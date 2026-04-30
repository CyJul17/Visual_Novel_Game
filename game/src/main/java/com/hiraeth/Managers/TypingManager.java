package com.hiraeth.Managers;
import javax.swing.*;
import com.hiraeth.Panels.GamePanel;

public class TypingManager {

    private JTextArea textArea;
    private Timer typeTimer;
    private boolean isTyping = false;
    private String fullText = "";
    private int charIndex = 0;
    private GamePanel game;
    private int typeSpeed = 30;


    public TypingManager(JTextArea textArea, GamePanel game) {

        this.textArea = textArea;
        this.game = game;
    }

    public void setTextSpeed (int speed) {

        this.typeSpeed = speed;
    }
    
    public void typeText(String text) {

        if (typeTimer != null && typeTimer.isRunning())  {
            
            typeTimer.stop();
        }

        this.fullText = text;
        this.charIndex = 0;
        this.isTyping = true;
        textArea.setText("");

        typeTimer = new Timer(typeSpeed, e -> {

            if (charIndex < fullText.length()) {

                textArea.append(String.valueOf(fullText.charAt(charIndex)));
                charIndex++;
            } else {

                ((Timer) e.getSource()).stop();
                isTyping = false;
            }
        });
        typeTimer.start();
    }
    
    public void continueType() {

        if (isTyping) {

            typeTimer.stop();
            textArea.setText(fullText);
            isTyping = false;
        } else {

            game.advanceDialogue();
        }
    }
    public boolean getTyping() {

        return isTyping;
    }
}