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
        minimizeButton.setPreferredSize(new Dimension(30, 30));
        minimizeButton.setFocusable(false);
        minimizeButton.setBackground(Palette.LIGHT_PINK);
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setBorderPainted(false);
        minimizeButton.setContentAreaFilled(false);

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
}
