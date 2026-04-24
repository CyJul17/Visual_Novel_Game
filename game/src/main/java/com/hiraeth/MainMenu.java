package com.hiraeth;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {

    private Image bgImage;
    private JLabel title;
    private JLabel shadow;
    private JButton buttons[];
    private ActionListener startAction;

    public MainMenu(ActionListener gameStart) {

        this.startAction = gameStart;
        this.setLayout(null);

        // loading the image:
        try {

            bgImage = new ImageIcon(getClass().getResource("/backgrounds/cliff.jpg")).getImage();
        } catch (Exception e) {

            System.out.println("Cannot access the background file...");
        }

        //setting up the shadow of the title
        shadow = new JLabel("HIRAETH");
        shadow.setFont(new Font("SERIF", Font.BOLD, 80));
        shadow.setForeground(new Color(0, 0, 0, 100));
        shadow.setBounds(55, 55, 500, 100);
        add(shadow);
        


        //setting up the title
        title = new JLabel("HIRAETH");
        title.setFont(new Font("SERIF", Font.BOLD, 80));
        title.setForeground(Color.WHITE);
        title.setBounds(50,50,500, 100);
        add(title);
        setComponentZOrder(title, 0);
        

        // buttons 
        String[] buttonLabel = {"Start", "Load Game", "Setting", "Quit Game"};
        buttons = new JButton[4];

        //button alignment
        int buttonAlign = 80;
        int buttonY = 180;
        int buttonGap = 70;

        //initialized the buttons 
        for (int i = 0; i < 4; i++) {

            int index = i;
            buttons[i] = buttonStyle(buttonLabel[i], buttonAlign, buttonY + (i * buttonGap), e -> buttonClicked(buttons[index]));
            add(buttons[i]);
        }
    } 

    //method

    private JButton buttonStyle(String btnText, int x, int y, ActionListener action) {

        //Making new button
        JButton btn = new JButton(btnText);
        btn.setBounds(x, y, 220, 55);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SanSerif", Font.BOLD, 24));

        //Style
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(218, 165, 32));
        btn.setBorder(BorderFactory.createLineBorder(new Color(218, 165, 32), 3));
        btn.addActionListener(action);
        return btn;
    }

    private void buttonClicked(JButton button) {
        String getButtonText = button.getText();

        if ("Start".equals(getButtonText)) {
            startAction.actionPerformed(null);
        } else if ("Quit Game".equals(getButtonText)) {
            System.exit(0);
        } else if ("Setting".equals(getButtonText)) {
            JOptionPane.showMessageDialog(this, "Setting will be coming soon! Pray for the Dev ToT");
        } else if ("Load Game".equals(getButtonText)) {
            JOptionPane.showMessageDialog(this, "Load game will be coming soon!");
        }

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (bgImage != null) {

            g2d.drawImage(bgImage, 0, 0, 800, 600, this);
        }
         g2d.setColor(new Color(0, 0, 0, 150));
         g2d.drawString("", title.getX() + 5, title.getY() + 85);
    }
}