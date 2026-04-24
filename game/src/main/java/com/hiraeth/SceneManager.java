package com.hiraeth;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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

        //initializing the panels
        GamePanel game = new GamePanel();
        MainMenu menu = new MainMenu(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panel, "GAME");

                game.requestFocusInWindow();
                game.launchGame();
            }
        });
        //screen
        
        panel.add(menu, "MENU");
        panel.add(game, "GAME");
        add(panel);

        cardLayout.show(panel, "MENU");
        this.setVisible(true);


    }
}