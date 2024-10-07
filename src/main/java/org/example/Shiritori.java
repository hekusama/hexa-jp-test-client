package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Shiritori extends JFrame {

    private JFrame parent; // Reference to MainMenu

    public Shiritori(JFrame parentFrame) {

        parent = parentFrame;

        setUndecorated(true);
        setSize(800, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add the title bar to the frame
        add(new CustomTitleBar(this), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(MainMenu.GRAY);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(MainMenu.PINK);

        // Setup UI mainPanel
        setupUI(mainPanel, topPanel);
        add(mainPanel);

        setVisible(true);
    }

    private void setupUI(JPanel mainPanel, JPanel topPanel) {

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(MainMenu.PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(true);
        backButton.setFocusPainted(false); // Remove focus border on the button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setVisible(true); // Show the main menu
                dispose(); // Close the Shiritori window to free resources
            }
        });

        // Top panel
        topPanel.add(backButton, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
    }
}
