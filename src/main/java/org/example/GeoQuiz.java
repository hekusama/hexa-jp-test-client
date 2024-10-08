package org.example;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class GeoQuiz extends JFrame {

    //Window dimming
    // Create the dimming panel
    private static JPanel dimmingPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 50% opacity
            g2d.setColor(Color.BLACK);  // Black color for dimming
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    private JPanel glassPane = (JPanel) getGlassPane();

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
    private static JButton backButton;
    private static JButton timedButton;
    private static ProgressTracker progressTracker;

    private static String timeframe;
    private JLabel timerLabel;
    private Timer countdownTimer;
    private long timeRemaining; // Time in seconds

    private JFrame parent; // Reference to MainMenu
    private static final String SAVE = "geo_save.txt";  // Save file location
    private static final File HS = new File("geo_hs.txt");  // Highscore file location

    private static ArrayList<Integer> hsArray;

    public GeoQuiz(JFrame parentFrame) {

        parent = parentFrame;

        setUndecorated(true);
        setSize(800, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Setup dimming panel
        // Add a MouseAdapter to consume all mouse events and prevent interaction
        glassPane.addMouseListener(new MouseAdapter() {});
        glassPane.addMouseMotionListener(new MouseAdapter() {});
        glassPane.addKeyListener(new KeyAdapter() {});

        dimmingPanel.setOpaque(false);
        glassPane.setVisible(false);
        glassPane.setOpaque(false);  // Make it non-opaque to allow the dimming effect
        glassPane.setLayout(new BorderLayout());  // Set a layout manager
        glassPane.add(dimmingPanel);

        // Add the title bar to the frame
        add(new CustomTitleBar(this), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Palette.GRAY);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Palette.PINK);

        // Load data from the separate files
        DataLoader dataLoader = new DataLoader("cities.txt", "prefectures.txt", "stations.txt", "towns.txt");
        progressTracker = new ProgressTracker(dataLoader);
        history = new ArrayList<>();
        usedEntries = new HashSet<>();  // Initialize the set to track used inputs

        // Setup other UI components (input, checkboxes, progress bars, etc.)
        setupUI(mainPanel, topPanel, dataLoader);

        // Load progress from SAVE file (if it exists)
        loadProgress();

        // Set up a window listener to SAVE progress on close
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
    private void setupUI(JPanel mainPanel, JPanel topPanel, DataLoader dataLoader) {

        // Top panel with the Back button
        backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Palette.PINK);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false); // Remove focus border on the button
        backButton.addActionListener(e -> {
            saveProgress();
            parent.setVisible(true); // Show the main menu
            dispose(); // Close the quiz window to free resources
        });
        topPanel.add(backButton, BorderLayout.WEST); // Place the back button at the top left

        // Title
        JLabel title = new JLabel("          Japan geography");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(title, BorderLayout.CENTER);

        // Top Subpanel for Timer, Timed and Reset
        JPanel topSub = new JPanel();
        topSub.setLayout(new GridLayout(1, 3));
        topSub.setBackground(Palette.PINK);

        //Timed mode
        timerLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Set the font
        timerLabel.setVisible(false); // Hidden until Timed Mode starts
        topSub.add(timerLabel); // Add timer at the top

        // Add a new Timed button next to the Reset button
        timedButton = new JButton("Timed");
        timedButton.setForeground(Color.WHITE);
        timedButton.setBackground(Palette.PINK);
        timedButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timedButton.setBorderPainted(false);
        timedButton.setFocusPainted(false); // Remove focus border on the button
        timedButton.setPreferredSize(new Dimension(100, 50));
        timedButton.addActionListener(e -> showTimedOptionsWindow());

        topSub.add(timedButton);  // Add the button to your bottom panel

        // Add a Reset Progress button to reset the progress and history
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 50));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(Palette.PINK);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setBorderPainted(false);
        resetButton.setContentAreaFilled(true);
        resetButton.setFocusPainted(false); // Remove focus border on the button
        resetButton.addActionListener(e -> {
            confirm(0);
        }); // Call resetProgress method on click

        topSub.add(resetButton);
        topPanel.add(topSub, BorderLayout.EAST); // Place Reset button at the top right

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Japan Map
        Image japanLoad = ImageLoader.loadImage("japan.png");
        ImageIcon japan = new ImageIcon(japanLoad);
        JLabel japanL = new JLabel(japan);

        // Input and status panel (center)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout()); // Adjust to 3 rows: Input field, status, checkboxes
        inputPanel.setForeground(Color.WHITE);
        inputPanel.setBackground(Palette.GRAY);

        inputPanel.add(japanL);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 30));
        inputField.setForeground(Color.WHITE);
        inputField.setBackground(Palette.LIGHTER_GRAY);
        inputField.setBorder(new LineBorder(Palette.LIGHT_GRAY, 2));
        inputPanel.add(inputField);

        statusLabel = new JLabel("Enter a town, city, prefecture, or train station.");
        statusLabel.setForeground(Color.WHITE);
        inputPanel.add(statusLabel);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Checkbox panel (below input field and status)
        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new FlowLayout());
        checkBoxPanel.setBackground(Palette.GRAY);

        townCheckBox = new JCheckBox("Towns", true);  // Default to checked
        cityCheckBox = new JCheckBox("Cities", true);  // Default to checked
        prefectureCheckBox = new JCheckBox("Prefectures", true);  // Default to checked
        stationCheckBox = new JCheckBox("Stations", true);  // Default to checked

        townCheckBox.setBackground(Palette.GRAY);
        cityCheckBox.setBackground(Palette.GRAY);
        prefectureCheckBox.setBackground(Palette.GRAY);
        stationCheckBox.setBackground(Palette.GRAY);

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

        townProgressBar.setForeground(Palette.RED);
        cityProgressBar.setForeground(Palette.YELLOW);
        prefectureProgressBar.setForeground(Palette.BLUE);
        stationProgressBar.setForeground(Palette.PURPLE);

        townProgressBar.setBackground(Palette.LIGHT_GRAY);
        cityProgressBar.setBackground(Palette.LIGHT_GRAY);
        prefectureProgressBar.setBackground(Palette.LIGHT_GRAY);
        stationProgressBar.setBackground(Palette.LIGHT_GRAY);

        townProgressLabel = new JLabel("Stations Progress: 0/" + dataLoader.getTowns().size());
        cityProgressLabel = new JLabel("Cities Progress: 0/" + dataLoader.getCities().size());
        prefectureProgressLabel = new JLabel("Prefectures Progress: 0/" + dataLoader.getPrefectures().size());
        stationProgressLabel = new JLabel("Stations Progress: 0/" + dataLoader.getStations().size());

        townProgressLabel.setForeground(Palette.LIGHTER_GRAY);
        cityProgressLabel.setForeground(Palette.LIGHTER_GRAY);
        prefectureProgressLabel.setForeground(Palette.LIGHTER_GRAY);
        stationProgressLabel.setForeground(Palette.LIGHTER_GRAY);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(4, 2));
        progressPanel.setBackground(Palette.DARK_GRAY);

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
        historyPanel.setBackground(Palette.GRAY);
        historyPanel.setBorder(new LineBorder(Palette.GRAY, 3));

        historyTextArea = new JTextArea(15, 20);
        historyTextArea.setEditable(false);
        historyTextArea.setForeground(Color.WHITE);
        historyTextArea.setBackground(Palette.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);

        // Filter options for the history
        filterComboBox = new JComboBox<>(new String[]{"All", "Cities", "Prefectures", "Stations", "Towns"});
        filterComboBox.setForeground(Color.WHITE);
        filterComboBox.setBackground(Palette.LIGHTER_GRAY);
        filterComboBox.addActionListener(e -> updateHistoryDisplay());

        // Search field for filtering by name
        searchField = new JTextField();
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(Palette.LIGHTER_GRAY);
        searchField.setColumns(10);
        searchField.addActionListener(e -> updateHistoryDisplay());

        // Filter and search panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(Palette.GRAY);
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
            townProgressBar.setForeground(Palette.GREEN);
            townProgressLabel.setText("Towns Progress: Completed!");
        }

        if (cityProgress == progressTracker.getCityTotal()) {
            cityProgressBar.setForeground(Palette.GREEN);
            cityProgressLabel.setText("Cities Progress: Completed!");
        }

        if (prefectureProgress == progressTracker.getPrefectureTotal()) {
            prefectureProgressBar.setForeground(Palette.GREEN);
            prefectureProgressLabel.setText("Prefectures Progress: Completed!");
        }

        if (stationProgress == progressTracker.getStationTotal()) {
            stationProgressBar.setForeground(Palette.GREEN);
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

    /**
     * Confirmation window
     * 0 = reset
     * 1 = end timed run
     */
    private void confirm(int type) {
        JFrame confirmWindow = new JFrame();
        confirmWindow.setSize(300, 180);
        confirmWindow.setUndecorated(true);
        confirmWindow.setLocationRelativeTo(null);

        // MainFrame dimming
        glassPane.setVisible(true);
        // Add WindowListener to detect when the frame is disposed
        confirmWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                glassPane.setVisible(false);
            }
        });

        // Add the title bar to the frame
        confirmWindow.add(new CustomTitleBar(confirmWindow, true), BorderLayout.NORTH);

        // Title
        JPanel titleBase = new JPanel();
        titleBase.setBackground(Palette.PINK);
        JLabel title = new JLabel();
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);

        if (type == 0) {
            title.setText("Reset?");
        } else if (type == 1) {
            title.setText("End run?");
        }

        titleBase.add(title);

        confirmWindow.add(titleBase);

        JPanel optionBase = new JPanel(new GridLayout(1, 2));
        optionBase.setBackground(Palette.DARK_GRAY);

        JButton yesButton = new JButton("Yes");
        yesButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        yesButton.setPreferredSize(new Dimension(150, 60));
        yesButton.setForeground(Color.WHITE);
        yesButton.setBackground(Palette.DARK_GRAY);
        yesButton.setBorderPainted(false);
        yesButton.setFocusPainted(false);

        if (type == 0) {
            yesButton.addActionListener(e -> {
                resetProgress();
                confirmWindow.dispose();
            });
        } else if (type == 1) {
            yesButton.addActionListener(e -> {
                countdownTimer.stop();
                confirmWindow.dispose();
                showTimedScore();
            });
        }

        optionBase.add(yesButton);

        JButton noButton = new JButton("No");
        noButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        noButton.setPreferredSize(new Dimension(150, 60));
        noButton.setForeground(Color.WHITE);
        noButton.setBackground(Palette.DARK_GRAY);
        noButton.setBorderPainted(false);
        noButton.setFocusPainted(false);
        noButton.addActionListener(e -> {
            confirmWindow.dispose();
        });
        optionBase.add(noButton);
        confirmWindow.add(optionBase, BorderLayout.SOUTH);

        confirmWindow.setVisible(true);
    }

    // Method to reset progress, clear history, and reset progress bars
    private static void resetProgress() {
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
        statusLabel.setText("Enter a town, city, prefecture, or train station.");
    }

    // Method to SAVE progress to a file
    private static void saveProgress() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE))) {
            // Write each history entry to the SAVE file
            for (String entry : history) {
                writer.write(entry);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving progress: " + e.getMessage());
        }
    }

    // Method to load progress from the SAVE file
    private static void loadProgress() {
        if (Files.exists(Paths.get(SAVE))) {
            try (BufferedReader reader = new BufferedReader(new FileReader(SAVE))) {
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

    private void showTimedOptionsWindow() {
        // Create a new JFrame for the options window
        JFrame optionsWindow = new JFrame();
        optionsWindow.setSize(400, 400);
        optionsWindow.setUndecorated(true);
        optionsWindow.setLocationRelativeTo(null);

        // MainFrame dimming
        glassPane.setVisible(true);
        // Add WindowListener to detect when the frame is disposed
        optionsWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                glassPane.setVisible(false);
            }
        });

        // Add the title bar to the frame
        optionsWindow.add(new CustomTitleBar(optionsWindow, true), BorderLayout.NORTH);

        // Panel layouts
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel titleBase = new JPanel();
        JPanel startBase = new JPanel();
        JPanel timeOptions = new JPanel(new GridLayout(1, 2));
        JPanel t1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel t2 = new JPanel();
        t2.setLayout(new BoxLayout(t2, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("Timed Mode", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        titleBase.setBackground(Palette.PINK);
        titleBase.add(title);
        titlePanel.add(titleBase, BorderLayout.NORTH);

        // Radio buttons
        JRadioButton standardTimeButton = new JRadioButton("Standard time");
        JRadioButton customTimeButton = new JRadioButton("Custom time");
        standardTimeButton.setForeground(Color.WHITE);
        customTimeButton.setForeground(Color.WHITE);
        standardTimeButton.setBackground(Palette.GRAY);
        customTimeButton.setBackground(Palette.GRAY);
        standardTimeButton.setFocusPainted(false);
        customTimeButton.setFocusPainted(false);

        ButtonGroup timeModeGroup = new ButtonGroup();
        timeModeGroup.add(standardTimeButton);
        timeModeGroup.add(customTimeButton);
        standardTimeButton.setPreferredSize(new Dimension(150, 30));
        customTimeButton.setPreferredSize(new Dimension(150, 30));
        t1.add(standardTimeButton);

        // Drop-down for standard times and high score display
        String[] standardTimes = {"1:00", "3:00", "5:00", "10:00", "30:00", "1:00:00"};
        JComboBox<String> timeDropdown = new JComboBox<>(standardTimes);
        timeDropdown.setPreferredSize(new Dimension(150, 30));
        timeDropdown.setForeground(Color.WHITE);
        timeDropdown.setBackground(Palette.LIGHTER_GRAY);
        timeDropdown.setBorder(new LineBorder(Palette.LIGHT_GRAY, 2));
        t1.add(timeDropdown);

        // Add custom time button after
        t1.add(customTimeButton);

        // Custom time input
        JTextField customTimeField = new JTextField("hh:mm:ss");
        customTimeField.setPreferredSize(new Dimension(150, 30));
        customTimeField.setForeground(Color.WHITE);
        customTimeField.setBackground(Palette.LIGHTER_GRAY);
        customTimeField.setBorder(new LineBorder(Palette.LIGHT_GRAY, 2));
        t1.add(customTimeField);

        // Highscores
        String hsFormat = "";
        boolean error = false;
        if (HS.exists() && !FileUtil.isFileEmpty(HS)) {
            String highscores = Encryptor.readEncryptedData(HS);
            hsArray = new ArrayList<>();
            int index = 0;
            while (index < highscores.length()) {
                int nextSpace = highscores.indexOf(' ', index);
                if (nextSpace == -1) {  // No more spaces, read until the end of the string
                    nextSpace = highscores.length();
                }
                try {
                    // Parse the integer score between current index and next space
                    hsArray.add(Integer.parseInt(highscores.substring(index, nextSpace)));
                    // Update index to move past the current score
                    index = nextSpace + 1;
                } catch (NumberFormatException e) {
                    hsFormat = "<html>Error: Cannot read<br>" + HS + "</html>";
                    error = true;
                    break;
                }
            }
            if (!error) {
                hsFormat = "<html>";
                int max = 0;
                for (int i = 0; i < hsArray.size(); i++) {
                    switch (i) {
                        case 0:
                            hsFormat += "1 min: ";
                            break;
                        case 1:
                            hsFormat += "3 min: ";
                            break;
                        case 2:
                            hsFormat += "5 min: ";
                            break;
                        case 3:
                            hsFormat += "10 min: ";
                            break;
                        case 4:
                            hsFormat += "30 min: ";
                            break;
                        case 5:
                            hsFormat += "1 hour: ";
                            break;
                    }
                    hsFormat += hsArray.get(i) + "<br>";
                    if (hsArray.get(i) > max)
                        max = hsArray.get(i);
                }
                hsFormat += "Max: " + max + "</html>";
            }
        } else {
            if (!HS.exists()) {
                try {
                    HS.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            hsFormat = "<html>1 min: 0<br>" +
                    "3 min: 0<br>" +
                    "5 min: 0<br>" +
                    "10 min: 0<br>" +
                    "30 min: 0<br>" +
                    "1 hour: 0<br>" +
                    "Max: 0</html>";
        }
        JLabel hsTitle = new JLabel("Highscores: ");
        hsTitle.setForeground(Color.WHITE);
        hsTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel hsLabel = new JLabel(hsFormat);
        hsLabel.setForeground(Color.WHITE);
        hsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        t2.add(hsTitle);
        t2.add(hsLabel);

        // Start button (enabled only when a valid option is selected)
        JButton startButton = new JButton("Start");
        startButton.setEnabled(false);  // Disabled until a valid time is selected
        startButton.setPreferredSize(new Dimension(150, 40));
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(Palette.DARK_GRAY);
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);

        startBase.setBackground(Palette.DARK_GRAY);

        startBase.add(startButton);
        titlePanel.add(startBase, BorderLayout.SOUTH);

        // Add Action Listeners for enabling the start button based on selection
        standardTimeButton.addActionListener(e -> {
            customTimeField.setEnabled(false);
            timeDropdown.setEnabled(true);
            startButton.setEnabled(true);  // Valid when any standard time is selected
        });

        customTimeButton.addActionListener(e -> {
            customTimeField.setEnabled(true);
            timeDropdown.setEnabled(false);
            startButton.setEnabled(false);  // Remains disabled until valid custom time is entered
        });

        customTimeField.addActionListener(e -> {
            if (isValidCustomTime(customTimeField.getText())) {
                startButton.setEnabled(true);  // Enable start button if the input is valid
            } else {
                startButton.setEnabled(false);
            }
        });

        startButton.addActionListener(e -> {
            // Start the timed mode with either standard or custom time
            if (standardTimeButton.isSelected()) {
                timeframe = (String) timeDropdown.getSelectedItem();
                startTimedMode(parseTimeToMilliseconds(timeframe));
            } else if (customTimeButton.isSelected()) {
                startTimedMode(parseTimeToMilliseconds(customTimeField.getText()));
                timeframe = customTimeField.getText();
            }
            optionsWindow.dispose();  // Close the options window
        });

        t1.setBackground(Palette.GRAY);
        t2.setBackground(Palette.GRAY);

        timeOptions.add(t1);
        timeOptions.add(t2);

        titlePanel.add(timeOptions);
        optionsWindow.add(titlePanel);
        optionsWindow.setVisible(true);
    }

    // Helper function to validate custom time input
    private boolean isValidCustomTime(String time) {
        // Validate the custom time format (hh:mm:ss), hours should not exceed 24, and minutes/seconds shouldn't exceed 59
        return time.matches("([01]?\\d|2[0-3]):[0-5]?\\d:[0-5]?\\d") || time.matches("[0-5]?\\d:[0-5]?\\d");
    }

    // Helper function to convert time (e.g., 1:00 or 1:00:00) to milliseconds
    private long parseTimeToMilliseconds(String time) {
        // Convert standard time format (mm:ss or hh:mm:ss) to milliseconds
        String[] parts = time.split(":");
        long milliseconds = 0;
        if (parts.length == 2) {  // Format mm:ss
            milliseconds += Integer.parseInt(parts[0]) * 60000;  // minutes to ms
            milliseconds += Integer.parseInt(parts[1]) * 1000;   // seconds to ms
        } else if (parts.length == 3) {  // Format hh:mm:ss
            milliseconds += Integer.parseInt(parts[0]) * 3600000; // hours to ms
            milliseconds += Integer.parseInt(parts[1]) * 60000;   // minutes to ms
            milliseconds += Integer.parseInt(parts[2]) * 1000;    // seconds to ms
        }
        return milliseconds;
    }

    private void startTimedMode(long timeInMillis) {
        // Load the quiz with the temporary SAVE file
        loadQuizTimed();

        // Start the countdown timer
        startTimedModeTimer(timeInMillis / 1000);

        // Hide the "Back" and "Timed" buttons, and replace "Reset" with "End"
        backButton.setVisible(false);
        timedButton.setVisible(false);
        resetButton.setText("End");
        resetButton.removeActionListener(resetButton.getActionListeners()[0]);
        resetButton.addActionListener(e -> {
            confirm(1);
        });
    }

    // Function to load quiz data with the SAVE file
    private void loadQuizTimed() {
        saveProgress();
        resetProgress();
    }

    // Function to show score after timed mode ends
    private void showTimedScore() {
        int score = calculateScore();  // Calculate the score based on progress

        // Display the score and a close button
        JFrame scoreWindow = new JFrame("Timed Mode Results");
        scoreWindow.setSize(400, 300);
        scoreWindow.setLayout(new BorderLayout());
        scoreWindow.setUndecorated(true);
        scoreWindow.setLocationRelativeTo(null);

        scoreWindow.add(new CustomTitleBar(scoreWindow), BorderLayout.NORTH);

        // MainFrame dimming
        glassPane.setVisible(true);
        // Add WindowListener to detect when the frame is disposed
        scoreWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                glassPane.setVisible(false);
            }
        });

        JPanel innerPanel = new JPanel(new BorderLayout());

        // Title
        JPanel titleBase = new JPanel();
        titleBase.setBackground(Palette.PINK);
        JLabel title = new JLabel("Results");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        titleBase.add(title);
        innerPanel.add(titleBase, BorderLayout.NORTH);

        // Show highscore
        int highscore = 0;
        if (hsArray != null) {
            highscore = switch (timeframe) {
                case "01:00", "1:00" -> hsArray.get(0);
                case "03:00", "3:00" -> hsArray.get(1);
                case "05:00", "5:00" -> hsArray.get(2);
                case "10:00" -> hsArray.get(3);
                case "30:00" -> hsArray.get(4);
                case "01:00:00", "1:00:00" -> hsArray.get(5);
                default -> 0;
            };
        }

        JPanel scoreBase = new JPanel();
        scoreBase.setBackground(Palette.GRAY);
        JLabel scoreLabel = new JLabel("<html>Score: " + score + "<br> Time: " + timeframe + "<br>Highscore: " + highscore + "</html>", JLabel.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        scoreBase.add(scoreLabel);
        innerPanel.add(scoreBase);
        scoreWindow.add(innerPanel);

        JPanel closeBase = new JPanel();
        closeBase.setBackground(Palette.DARK_GRAY);
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        closeButton.setPreferredSize(new Dimension(150, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(Palette.DARK_GRAY);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> {
            scoreWindow.dispose();
            resetToNormalMode();  // Reset to the normal interface
        });
        closeBase.add(closeButton);
        scoreWindow.add(closeBase, BorderLayout.SOUTH);

        scoreWindow.setVisible(true);

        // Store highscore
        if (hsArray == null) {
            hsArray = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                hsArray.add(0);
            }
        }
        switch (timeframe) {
            case "01:00", "1:00":
                if (score > hsArray.get(0))
                    hsArray.set(0, score);
                break;
            case "03:00", "3:00":
                if (score > hsArray.get(1))
                    hsArray.set(1, score);
                break;
            case "05:00", "5:00":
                if (score > hsArray.get(2))
                    hsArray.set(2, score);
                break;
            case "10:00":
                if (score > hsArray.get(3))
                    hsArray.set(3, score);
                break;
            case "30:00":
                if (score > hsArray.get(4))
                    hsArray.set(4, score);
                break;
            case "01:00:00", "1:00:00":
                if (score > hsArray.get(5))
                    hsArray.set(5, score);
                break;
            default:
                break;
        }
        String hsList = "";
        for (int i : hsArray) {
            hsList += i + " ";
        }
        Encryptor.writeEncryptedData(HS, hsList);
    }

    // Function to calculate score based on progress
    private int calculateScore() {
        // Add up the number of named items from each category
        return progressTracker.getTownProgress() + progressTracker.getCityProgress() + progressTracker.getPrefectureProgress() + progressTracker.getStationProgress();
    }

    // Reset the interface back to the normal mode after the timed mode ends
    private void resetToNormalMode() {
        backButton.setVisible(true);
        timedButton.setVisible(true);
        timerLabel.setVisible(false);
        resetButton.setText("Reset");
        resetButton.removeActionListener(resetButton.getActionListeners()[0]);  // Remove the "End" button listener
        resetButton.addActionListener(e -> {
            confirm(0);
        }); // Call resetProgress method on click
        resetProgress();
        loadProgress();
    }
    private void startTimedModeTimer(long timeInSeconds) {
        timeRemaining = timeInSeconds;

        // Show the timer label when the quiz starts
        timerLabel.setVisible(true);

        // Initialize and start the countdown timer
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerLabel();

                if (timeRemaining < 0) {
                    countdownTimer.stop();
                    showTimedScore(); // Show results when time's up
                }
            }
        });

        countdownTimer.start(); // Start the timer
    }

    // Update the timer label (mm:ss format)
    private void updateTimerLabel() {
        long hours = timeRemaining / 3600;
        long minutes = timeRemaining / 60 - hours * 60;
        long seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        timeRemaining--;
    }
}
