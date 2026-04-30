package com.hiraeth.Managers;
import javax.swing.*;

import com.hiraeth.MainMenu;
import com.hiraeth.Panels.GamePanel;
import com.hiraeth.Panels.SettingPanel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        //Component Listener to move components when minimize or maximize.
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getWidth();
                panel.setBounds(0,0, w, h);
            }
        });
            
        

        //initializing the panels
        GamePanel game = new GamePanel();
        SettingPanel settings = new SettingPanel(game);
        MainMenu menu = new MainMenu(e -> {

            String command = e.getActionCommand();
            cardLayout.show(panel, "GAME");
            if ("LOAD_GAME".equals(command)) {

                game.continueGame();
            } else {

                game.startNewGame();
            }
        });
       menu.setSettings(settings);

        //screen
        
       panel.add(createCenteredWrapper(menu), "MENU");
       panel.add(createCenteredWrapper(game), "GAME");

       this.add(panel);

        cardLayout.show(panel, "MENU");
        this.setVisible(true);


    }

    private JPanel createCenteredWrapper(JPanel content) {

        JPanel wrapper = new JPanel(new GridBagLayout());

            wrapper.setBackground(Color.BLACK);
            // wrapper.add(content, BorderLayout.CENTER);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            content.setPreferredSize(new Dimension(800, 600));
            content.setMinimumSize(new Dimension(800, 600));;
            wrapper.add(content, gbc);

            return wrapper;
    }
}