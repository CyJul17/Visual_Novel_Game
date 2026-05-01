package com.hiraeth.Managers;
import javax.swing.*;

import com.hiraeth.MainMenu;
import com.hiraeth.Panels.GamePanel;
import com.hiraeth.Panels.SettingPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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


public class SceneManager extends JFrame implements ActionListener {
    
    private MusicManager musicMan = new MusicManager();
    private CardLayout cardLayout = new CardLayout();
    private JPanel panel = new JPanel(cardLayout);
    private String currentView = "MENU";
    private GamePanel game;
    private MainMenu menu;

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
        game = new GamePanel(this);
        SettingPanel settings = new SettingPanel(game);
        menu = new MainMenu(this);
        menu.setSettings(settings);

        //screen
        
       panel.add(createCenteredWrapper(menu), "MENU");
       panel.add(createCenteredWrapper(game), "GAME");

       this.add(panel);
        showMain();
        cardLayout.show(panel, "MENU");
        this.setVisible(true);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

      switch (command) {
        case "ESC_PRESSED":

            escape();
            break;
        case "NEW_GAME":

            showGame();
            game.startNewGame();
            break;
        case "LOAD_GAME":

            showGame();
            game.continueGame();
            break;
      
        default: //Useless line ToT
            System.out.println("None of the choice are performed.");
            break;
      }

    }

    private void showMain() {

        currentView = "MENU";
        cardLayout.show(panel, "MENU");
        musicMan.playBGM("Far Away(intro).wav");
    }

    private void showGame() {

        currentView = "GAME";
        cardLayout.show(panel, "GAME");
        musicMan.stopBGM();
    }

    private JPanel createCenteredWrapper(JPanel content) {

        JPanel wrapper = new JPanel(new GridBagLayout());

            wrapper.setBackground(Color.BLACK);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            content.setPreferredSize(new Dimension(800, 600));
            content.setMinimumSize(new Dimension(800, 600));;
            wrapper.add(content, gbc);

            return wrapper;
    }

   
    private void escape() {
        if ("GAME".equals(currentView)) {

             int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to return to the Main Menu?",
                    "Return to Menu",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == 0) {

                    game.stopMusic();
                    showMain();
                }

        } else if ("MENU". equals(currentView)) {

              int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to return to exit the game?",
                    "Exit Game",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == 0) {

                    System.exit(0);
                }
        }
    }
}