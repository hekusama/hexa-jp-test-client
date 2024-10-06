package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Shiritori extends JFrame {

    private JFrame parent; // Reference to MainMenu

    public Shiritori(JFrame parentFrame) {
        parent = parentFrame;
        setTitle("Empty");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Back button to return to main menu
        JButton backButton = new JButton("Back");
        setupUI(backButton);

        // Top panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Placeholder content in the center (can be expanded later)
        JLabel placeholderLabel = new JLabel("You find yourself in a strange place...", SwingConstants.CENTER);
        add(placeholderLabel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void setupUI(JButton backButton) {
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setVisible(true); // Show the main menu
                dispose(); // Close the Shiritori window to free resources
            }
        });
    }
}
