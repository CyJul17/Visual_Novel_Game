package com.hiraeth;
import javax.swing.*;
import java.awt.*;

/*
*
* This is the frame of the visual novel.
* It uses a CardLayout to manage different scenes,
* allowing for easy transition between the scens of the
* story.
*
*/


public class SceneManager extends JFrame {
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel panel = new JPanel(cardLayout);

    //Constructor 
    public SceneManager() {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Hiraeth");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        
        //screen
        panel.add(new GamePanel(), "Hiraeth");
        this.add(panel);
        this.setVisible(true);


    }
}