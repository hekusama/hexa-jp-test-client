package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BackgroundPanel extends JPanel {

    private Image backgroundImage;

    public BackgroundPanel(Image image) {
        this.backgroundImage = image;
    }

    // Constructor to load the background image
    public BackgroundPanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath)); // Load the image file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Override the paintComponent method to draw the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
