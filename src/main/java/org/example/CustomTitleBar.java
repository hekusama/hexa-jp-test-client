package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomTitleBar extends JPanel{

    private int mouseX, mouseY;

    public CustomTitleBar(JFrame frame) {
        setBackground(Palette.LIGHT_PINK);
        setLayout(new BorderLayout());

        // Create a minimize button
        JButton minimizeButton = new JButton("_");
        minimizeButton.setPreferredSize(new Dimension(50, 30));
        minimizeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        minimizeButton.setFocusable(false);
        minimizeButton.setBackground(Palette.LIGHT_PINK);
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setBorderPainted(false);

        minimizeButton.addActionListener(e -> frame.setState(JFrame.ICONIFIED)); // Minimize the window
        add(minimizeButton, BorderLayout.EAST);

        // Add mouse listeners for dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Calculate new position
                int x = e.getXOnScreen() - mouseX;
                int y = e.getYOnScreen() - mouseY;
                frame.setLocation(x, y);
            }
        });
    }

    public CustomTitleBar(JFrame frame, boolean withX) {
        this(frame);

        if (withX) {
            // SubPanel
            JPanel subPanel = new JPanel(new GridLayout(1, 2));

            // Create a minimize button
            JButton minimizeButton = new JButton("_");
            minimizeButton.setPreferredSize(new Dimension(50, 30));
            minimizeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            minimizeButton.setFocusable(false);
            minimizeButton.setBackground(Palette.LIGHT_PINK);
            minimizeButton.setForeground(Color.WHITE);
            minimizeButton.setBorderPainted(false);

            minimizeButton.addActionListener(e -> frame.setState(JFrame.ICONIFIED)); // Minimize the window
            subPanel.add(minimizeButton);

            // Create a close button (X) if withX is true
            JButton closeButton = new JButton("X");
            closeButton.setPreferredSize(new Dimension(50, 30));
            closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            closeButton.setFocusable(false);
            closeButton.setBackground(Palette.LIGHT_PINK);
            closeButton.setForeground(Color.WHITE);
            closeButton.setBorderPainted(false);

            closeButton.addActionListener(e -> frame.dispose()); // Close the window
            subPanel.add(closeButton);

            add(subPanel, BorderLayout.EAST);
        }
    }
}
