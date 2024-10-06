package org.example;

import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public static Image loadImage(String fileName) {
        Image image = null;
        try (InputStream inputStream = ImageLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream != null) {
                // Use ImageIO to read the image from the InputStream
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                image = bufferedImage;
            } else {
                System.out.println("Image not found: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public static void main(String[] args) {
        // Example usage
        Image myImage = loadImage("myImage.png"); // Make sure to use the correct path
        if (myImage != null) {
            // Do something with the image, like displaying it
            System.out.println("Image loaded successfully!");
        }
    }
}
