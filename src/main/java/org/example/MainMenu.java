package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {

    public static final Color LIGHT_PINK = new Color(255, 199, 206);
    public static final Color PINK = new Color(255, 179, 186);
    public static final Color GRAY = new Color(55, 53, 63);
    public static final Color DARK_GRAY = new Color(35, 33, 43);
    public static final Color LIGHT_GRAY = new Color(95, 93, 103);

    public MainMenu() {
        setTitle("Japanese Practice");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // GeoQuiz panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(GRAY);

        // Top bar with title and icon
        JPanel topBar = new JPanel();
        topBar.setLayout(new BorderLayout());
        topBar.setPreferredSize(new Dimension(800, 80));  // Set preferred size for the top bar
        topBar.setBackground(PINK);
        JLabel titleLabel = new JLabel("Hexa JP test client");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        topBar.add(titleLabel, BorderLayout.WEST);

        // Image icon on top right
        ImageIcon icon = new ImageIcon("icon_image.png");
        Image image = icon.getImage();
        Image imageResize = image.getScaledInstance(60, 60,  java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(imageResize);
        JLabel iconLabel = new JLabel(icon); // Update with your image
        topBar.add(iconLabel, BorderLayout.EAST);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));
        buttonsPanel.setBackground(GRAY);

        // Geography quiz button (with image)
        ImageIcon geoImage = new ImageIcon("geo_button_image.png");
        ImageIcon geoScaled = getScaledIcon(geoImage, 1.1);

        JButton quizButton = new JButton(new ImageIcon("geo_button_image.png")); // Update with your image
        quizButton.setPreferredSize(new Dimension(300, 150));
        quizButton.setBorderPainted(false);
        quizButton.setContentAreaFilled(false);
        quizButton.setFocusPainted(false); // Remove focus border on the button

        // Add mouse listener for hover effect
        quizButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                quizButton.setIcon(geoScaled);  // Set the enlarged icon when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                quizButton.setIcon(geoImage);  // Set back to original icon when hover ends
            }
        });
        quizButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open GeoQuiz window
                GeoQuiz geoQuiz = new GeoQuiz(MainMenu.this);
                geoQuiz.setVisible(true);
                setVisible(false); // Hide main menu
            }
        });

        // Shiritori button (with image)
        ImageIcon shiritoriImage = new ImageIcon("blank_button.png");
        ImageIcon shiritoriScaled = getScaledIcon(shiritoriImage, 1.1);

        JButton shiritoriButton = new JButton(shiritoriImage); // Update with your image
        shiritoriButton.setPreferredSize(new Dimension(300, 150));
        shiritoriButton.setBorderPainted(false);
        shiritoriButton.setContentAreaFilled(false);
        shiritoriButton.setFocusPainted(false); // Remove focus border on the button

        // Add mouse listener for hover effect
        shiritoriButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                shiritoriButton.setIcon(shiritoriScaled);  // Set the enlarged icon when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                shiritoriButton.setIcon(shiritoriImage);  // Set back to original icon when hover ends
            }
        });
        shiritoriButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open GeoQuiz window
                Shiritori shiritori = new Shiritori(MainMenu.this);
                shiritori.setVisible(true);
                setVisible(false); // Hide main menu
            }
        });

        buttonsPanel.add(quizButton, BorderLayout.CENTER);
        buttonsPanel.add(shiritoriButton, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel);

        // Exit button on bottom right
        JButton exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(100, 50));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(LIGHT_GRAY);
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exitButton.setBorderPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setFocusPainted(false); // Remove focus border on the button
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(DARK_GRAY);
        bottomPanel.add(exitButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);
    }

    // Utility method to scale an ImageIcon
    private ImageIcon getScaledIcon(ImageIcon icon, double scale) {
        int width = (int) (icon.getIconWidth() * scale);
        int height = (int) (icon.getIconHeight() * scale);
        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
