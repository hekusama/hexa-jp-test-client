package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GeoQuiz extends JFrame {

    private static JProgressBar cityProgressBar;
    private static JProgressBar prefectureProgressBar;
    private static JProgressBar stationProgressBar;
    private static JProgressBar townProgressBar;

    private static JLabel cityProgressLabel;
    private static JLabel prefectureProgressLabel;
    private static JLabel stationProgressLabel;
    private static JLabel townProgressLabel;

    private static JCheckBox cityCheckBox;        // Initialize checkboxes
    private static JCheckBox prefectureCheckBox;  // Initialize checkboxes
    private static JCheckBox stationCheckBox;     // Initialize checkboxes
    private static JCheckBox townCheckBox;

    private static JTextField inputField;
    private static JLabel statusLabel;
    private static JTextArea historyTextArea;
    private static JComboBox<String> filterComboBox;
    private static JTextField searchField;
    private static List<String> history;
    private static Set<String> usedEntries;  // Tracks already entered inputs
    private static JButton resetButton;

    private JFrame parent; // Reference to MainMenu
    private static final String SAVE_FILE = "geo_save.txt";  // Save file location

    public GeoQuiz(JFrame parentFrame) {

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

        // Load data from the separate files
        DataLoader dataLoader = new DataLoader("cities.txt", "prefectures.txt", "stations.txt", "towns.txt");
        ProgressTracker progressTracker = new ProgressTracker(dataLoader);
        history = new ArrayList<>();
        usedEntries = new HashSet<>();  // Initialize the set to track used inputs

        // Setup other UI components (input, checkboxes, progress bars, etc.)
        setupUI(mainPanel, topPanel, progressTracker, dataLoader);

        // Load progress from save file (if it exists)
        loadProgress(progressTracker);

        // Set up a window listener to save progress on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // Save progress when the window is closing
                saveProgress();
                System.exit(0);
            }
        });

        add(mainPanel);
        setVisible(true);
    }

    // UI setup method to organize the rest of the components
    private void setupUI(JPanel mainPanel, JPanel topPanel, ProgressTracker progressTracker, DataLoader dataLoader) {

        // Top panel with the Back button
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(MainMenu.PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(true);
        backButton.setFocusPainted(false); // Remove focus border on the button
        backButton.addActionListener(e -> {
            saveProgress();
            parent.setVisible(true); // Show the main menu
            dispose(); // Close the quiz window to free resources
        });
        topPanel.add(backButton, BorderLayout.WEST); // Place the back button at the top left

        // Title
        JLabel title = new JLabel("             Endless Japanese geography quiz");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(title, BorderLayout.CENTER);

        // Add a Reset Progress button to reset the progress and history
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 50));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(MainMenu.PINK);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setBorderPainted(false);
        resetButton.setContentAreaFilled(true);
        resetButton.setFocusPainted(false); // Remove focus border on the button
        resetButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to reset your progress?",
                    "Confirm Reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            // Check if the user confirmed the reset
            if (response == JOptionPane.YES_OPTION) {
                resetProgress(progressTracker);  // Call the reset logic if confirmed
            }
        }); // Call resetProgress method on click
        topPanel.add(resetButton, BorderLayout.EAST); // Place Reset button at the top right

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Japan Map
        Image japanLoad = ImageLoader.loadImage("japan.png");
        ImageIcon japan = new ImageIcon(japanLoad);
        JLabel japanL = new JLabel(japan);

        // Input and status panel (center)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout()); // Adjust to 3 rows: Input field, status, checkboxes
        inputPanel.setForeground(Color.WHITE);
        inputPanel.setBackground(MainMenu.GRAY);

        inputPanel.add(japanL);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 30));
        inputField.setForeground(Color.WHITE);
        inputField.setBackground(MainMenu.LIGHTER_GRAY);
        inputField.setBorder(new LineBorder(MainMenu.LIGHT_GRAY, 2));
        inputPanel.add(inputField);

        statusLabel = new JLabel("Enter a town, city, prefecture, or train station.");
        statusLabel.setForeground(Color.WHITE);
        inputPanel.add(statusLabel);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Checkbox panel (below input field and status)
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new FlowLayout());
        checkBoxPanel.setBackground(MainMenu.GRAY);

        townCheckBox = new JCheckBox("Towns", true);  // Default to checked
        cityCheckBox = new JCheckBox("Cities", true);  // Default to checked
        prefectureCheckBox = new JCheckBox("Prefectures", true);  // Default to checked
        stationCheckBox = new JCheckBox("Stations", true);  // Default to checked

        townCheckBox.setBackground(MainMenu.GRAY);
        cityCheckBox.setBackground(MainMenu.GRAY);
        prefectureCheckBox.setBackground(MainMenu.GRAY);
        stationCheckBox.setBackground(MainMenu.GRAY);

        townCheckBox.setForeground(Color.WHITE);
        cityCheckBox.setForeground(Color.WHITE);
        prefectureCheckBox.setForeground(Color.WHITE);
        stationCheckBox.setForeground(Color.WHITE);

        checkBoxPanel.add(townCheckBox);
        checkBoxPanel.add(cityCheckBox);
        checkBoxPanel.add(prefectureCheckBox);
        checkBoxPanel.add(stationCheckBox);

        inputPanel.add(checkBoxPanel); // Add checkbox panel below the input field and status

        mainPanel.add(inputPanel, BorderLayout.CENTER); // Add the input panel (with checkboxes) to the center

        // Progress bars and labels
        townProgressBar = new JProgressBar(0, dataLoader.getTowns().size());
        cityProgressBar = new JProgressBar(0, dataLoader.getCities().size());
        prefectureProgressBar = new JProgressBar(0, dataLoader.getPrefectures().size());
        stationProgressBar = new JProgressBar(0, dataLoader.getStations().size());

        townProgressBar.setBorderPainted(false);
        cityProgressBar.setBorderPainted(false);
        prefectureProgressBar.setBorderPainted(false);
        stationProgressBar.setBorderPainted(false);

        townProgressBar.setForeground(MainMenu.P_RED);
        cityProgressBar.setForeground(MainMenu.P_YELLOW);
        prefectureProgressBar.setForeground(MainMenu.P_BLUE);
        stationProgressBar.setForeground(MainMenu.P_PURPLE);

        townProgressBar.setBackground(MainMenu.LIGHT_GRAY);
        cityProgressBar.setBackground(MainMenu.LIGHT_GRAY);
        prefectureProgressBar.setBackground(MainMenu.LIGHT_GRAY);
        stationProgressBar.setBackground(MainMenu.LIGHT_GRAY);

        townProgressLabel = new JLabel("Stations Progress: 0/" + dataLoader.getTowns().size());
        cityProgressLabel = new JLabel("Cities Progress: 0/" + dataLoader.getCities().size());
        prefectureProgressLabel = new JLabel("Prefectures Progress: 0/" + dataLoader.getPrefectures().size());
        stationProgressLabel = new JLabel("Stations Progress: 0/" + dataLoader.getStations().size());

        townProgressLabel.setForeground(MainMenu.LIGHTER_GRAY);
        cityProgressLabel.setForeground(MainMenu.LIGHTER_GRAY);
        prefectureProgressLabel.setForeground(MainMenu.LIGHTER_GRAY);
        stationProgressLabel.setForeground(MainMenu.LIGHTER_GRAY);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(4, 2));
        progressPanel.setBackground(MainMenu.DARK_GRAY);

        progressPanel.add(townProgressLabel);
        progressPanel.add(townProgressBar);
        progressPanel.add(cityProgressLabel);
        progressPanel.add(cityProgressBar);
        progressPanel.add(prefectureProgressLabel);
        progressPanel.add(prefectureProgressBar);
        progressPanel.add(stationProgressLabel);
        progressPanel.add(stationProgressBar);

        mainPanel.add(progressPanel, BorderLayout.SOUTH);

        // Scrollable history panel on the right
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.setBackground(MainMenu.GRAY);
        historyPanel.setBorder(new LineBorder(MainMenu.GRAY, 3));

        historyTextArea = new JTextArea(15, 20);
        historyTextArea.setEditable(false);
        historyTextArea.setForeground(Color.WHITE);
        historyTextArea.setBackground(MainMenu.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        // Filter options for the history
        filterComboBox = new JComboBox<>(new String[]{"All", "Cities", "Prefectures", "Stations", "Towns"});
        filterComboBox.setForeground(Color.WHITE);
        filterComboBox.setBackground(MainMenu.LIGHTER_GRAY);
        filterComboBox.addActionListener(e -> updateHistoryDisplay());

        // Search field for filtering by name
        searchField = new JTextField();
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(MainMenu.LIGHTER_GRAY);
        searchField.setColumns(10);
        searchField.addActionListener(e -> updateHistoryDisplay());

        // Filter and search panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(MainMenu.GRAY);
        JLabel filter = new JLabel("Filter:");
        filter.setForeground(Color.WHITE);
        filterPanel.add(filter);
        filterPanel.add(filterComboBox);
        JLabel search = new JLabel("Search:");
        search.setForeground(Color.WHITE);
        filterPanel.add(search);
        filterPanel.add(searchField);

        historyPanel.add(filterPanel, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(historyPanel, BorderLayout.EAST);

        // Submit with Enter key
        inputField.addActionListener(e -> handleInput(progressTracker));
    }

    // Handle input logic (used when Enter key is pressed)
    private static void handleInput(ProgressTracker progressTracker) {
        String input = inputField.getText().trim();  // Trim leading/trailing spaces
        input = normalizeInput(input);  // Normalize input by removing spaces and hyphens

        // Disallow empty inputs
        if (input.isEmpty()) {
            return;
        }

        // Check if input has been used already
        if (usedEntries.contains(input)) {
            statusLabel.setText("Input already submitted!");
            return;  // Exit without processing
        }

        boolean allowCities = cityCheckBox.isSelected();
        boolean allowPrefectures = prefectureCheckBox.isSelected();
        boolean allowStations = stationCheckBox.isSelected();
        boolean allowTowns = townCheckBox.isSelected();

        boolean match = progressTracker.checkName(input, allowCities, allowPrefectures, allowStations, allowTowns);
        String historyEntry;

        if (!match) {
            // If input is incorrect, display an error message and clear the input field without updating progress or history
            statusLabel.setText("Name not found! Please check for alternate spelling");
            inputField.setText("");  // Clear input field after incorrect submission
            return;  // Exit the method to block further actions
        }

        String flags = " ";
        if (progressTracker.isTown(input)) {
            flags += "(T) ";  // Mark as station
            progressTracker.incrementTownProgress();
        }
        if (progressTracker.isCity(input)) {
            flags += "(C) ";  // Mark as city
            progressTracker.incrementCityProgress();
        }
        if (progressTracker.isPrefecture(input)) {
            flags += "(P) ";  // Mark as prefecture
            progressTracker.incrementPrefectureProgress();
        }
        if (progressTracker.isStation(input)) {
            flags += "(S) ";  // Mark as station
            progressTracker.incrementStationProgress();
        }

        historyEntry = input + flags;
        statusLabel.setText("Entry added.");
        usedEntries.add(input);  // Add to used entries

        history.add(historyEntry);
        updateHistoryDisplay();
        updateProgress(progressTracker);
        inputField.setText("");  // Clear input field after each submission
    }

    // Utility method to normalize input by removing spaces and hyphens
    private static String normalizeInput(String input) {
        return input.replaceAll("[\\s-]", "").toLowerCase();
    }

    // Update progress bars and labels, and mark progress bar green if completed
    private static void updateProgress(ProgressTracker progressTracker) {
        int townProgress = progressTracker.getTownProgress();
        int cityProgress = progressTracker.getCityProgress();
        int prefectureProgress = progressTracker.getPrefectureProgress();
        int stationProgress = progressTracker.getStationProgress();

        townProgressBar.setValue(townProgress);
        cityProgressBar.setValue(cityProgress);
        prefectureProgressBar.setValue(prefectureProgress);
        stationProgressBar.setValue(stationProgress);

        townProgressLabel.setText("Towns Progress: " + townProgress + "/" + progressTracker.getTownTotal());
        cityProgressLabel.setText("Cities Progress: " + cityProgress + "/" + progressTracker.getCityTotal());
        prefectureProgressLabel.setText("Prefectures Progress: " + prefectureProgress + "/" + progressTracker.getPrefectureTotal());
        stationProgressLabel.setText("Stations Progress: " + stationProgress + "/" + progressTracker.getStationTotal());

        if (townProgress == progressTracker.getTownTotal()) {
            townProgressBar.setForeground(MainMenu.P_GREEN);
            townProgressLabel.setText("Towns Progress: Completed!");
        }

        if (cityProgress == progressTracker.getCityTotal()) {
            cityProgressBar.setForeground(MainMenu.P_GREEN);
            cityProgressLabel.setText("Cities Progress: Completed!");
        }

        if (prefectureProgress == progressTracker.getPrefectureTotal()) {
            prefectureProgressBar.setForeground(MainMenu.P_GREEN);
            prefectureProgressLabel.setText("Prefectures Progress: Completed!");
        }

        if (stationProgress == progressTracker.getStationTotal()) {
            stationProgressBar.setForeground(MainMenu.P_GREEN);
            stationProgressLabel.setText("Stations Progress: Completed!");
        }
    }

    // Update the history display with filtering options
    private static void updateHistoryDisplay() {
        String filter = (String) filterComboBox.getSelectedItem();
        String searchTerm = searchField.getText().trim().toLowerCase();

        StringBuilder filteredHistory = new StringBuilder();
        for (String entry : history) {
            // Determine if the entry matches the selected filter
            boolean matchesFilter = switch (filter) {
                case "Towns" -> entry.contains("(T)");
                case "Cities" -> entry.contains("(C)");
                case "Prefectures" -> entry.contains("(P)");
                case "Stations" -> entry.contains("(S)");
                default -> true; // "All" case
            };

            // Check if the entry matches the search term
            boolean matchesSearch = searchTerm.isEmpty() || entry.toLowerCase().contains(searchTerm);

            // Add entry to the filtered history if it matches both filter and search
            if (matchesFilter && matchesSearch) {
                filteredHistory.append(entry).append("\n");
            }
        }

        // Update the history text area with the filtered results
        historyTextArea.setText(filteredHistory.toString());
    }

    // Method to reset progress, clear history, and reset progress bars
    private static void resetProgress(ProgressTracker progressTracker) {
        // Reset progress variables in ProgressTracker
        progressTracker.resetProgress();

        // Reset progress bars
        townProgressBar.setValue(0);
        cityProgressBar.setValue(0);
        prefectureProgressBar.setValue(0);
        stationProgressBar.setValue(0);

        // Reset progress labels
        townProgressLabel.setText("Towns Progress: 0/" + progressTracker.getTownTotal());
        cityProgressLabel.setText("Cities Progress: 0/" + progressTracker.getCityTotal());
        prefectureProgressLabel.setText("Prefectures Progress: 0/" + progressTracker.getPrefectureTotal());
        stationProgressLabel.setText("Stations Progress: 0/" + progressTracker.getStationTotal());

        // Restore progress bar colors from green to default
        townProgressBar.setForeground(UIManager.getColor("ProgressBar.foreground"));
        cityProgressBar.setForeground(UIManager.getColor("ProgressBar.foreground"));
        prefectureProgressBar.setForeground(UIManager.getColor("ProgressBar.foreground"));
        stationProgressBar.setForeground(UIManager.getColor("ProgressBar.foreground"));

        // Clear history
        history.clear();
        usedEntries.clear();
        historyTextArea.setText("");  // Clear displayed history

        // Update status
        statusLabel.setText("Reset successful.");
    }

    // Method to save progress to a file
    private static void saveProgress() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // Write each history entry to the save file
            for (String entry : history) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving progress: " + e.getMessage());
        }
    }

    // Method to load progress from the save file
    private static void loadProgress(ProgressTracker progressTracker) {
        if (Files.exists(Paths.get(SAVE_FILE))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.add(line);  // Add the entry to history
                    usedEntries.add(normalizeInput(line.split(" ")[0]));  // Add to used entries (first part of line is the input)

                    // Update progress based on the flags (C), (P), (S), (T)
                    if (line.contains("(T)")) progressTracker.incrementTownProgress();
                    if (line.contains("(C)")) progressTracker.incrementCityProgress();
                    if (line.contains("(P)")) progressTracker.incrementPrefectureProgress();
                    if (line.contains("(S)")) progressTracker.incrementStationProgress();
                }

                // Update the history display and progress bars
                updateHistoryDisplay();
                updateProgress(progressTracker);
            } catch (IOException e) {
                System.out.println("Error loading progress: " + e.getMessage());
            }
        }
    }
}
