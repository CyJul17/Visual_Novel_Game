package com.hiraeth;
import java.awt.*;
import javax.swing.*;

import javax.swing.JPanel;

public class SettingPanel extends JPanel {
    
    public SettingPanel(GamePanel game) {

        this.setLayout(null);
        this.setBounds(100, 50, 600, 450);
        this.setBackground(new Color(0, 0, 0, 230));
        this.setVisible(false);

        // Title 
        JLabel title = new JLabel("SETTING", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 20, 600, 50);
        this.add(title);

        // Label for the spped
        JLabel speedLabel = new JLabel("Text Speed", SwingConstants.CENTER);
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setBounds(0, 90, 600, 30);
        this.add(speedLabel);

        // label to know what the delay was
        JLabel speedValue = new JLabel("Current Delay: 30");
        speedValue.setForeground(Color.WHITE);
        speedValue.setBounds(200, 170, 300, 30);
        this.add(speedValue);

        JSlider speedSlider = new JSlider(10, 100, 30);
        speedSlider.setBounds(150, 120, 300, 50);
        speedSlider.setOpaque(false);
        speedSlider.addChangeListener(e -> {

            int currentValue = speedSlider.getValue();
            game.setTextSpeed(currentValue);
            speedValue.setText("Current Delay: " + currentValue + "ms");
        });
        this.add(speedSlider);

        // Close Button 
        JButton exitButton = new JButton("Return to Menu");
        exitButton.setBounds(225, 350, 150, 40);
        exitButton.addActionListener(e -> {

            this.setVisible(false);
            game.requestFocusInWindow();
        });
        this.add(exitButton);
    }


}