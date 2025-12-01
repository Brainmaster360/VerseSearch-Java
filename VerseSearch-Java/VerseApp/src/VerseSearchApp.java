import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;


public class VerseSearchApp extends JFrame {

    private JTextField verseInput;
    private JTextArea outputArea;
    private JButton fetchButton, clearButton, historyButton, saveHistoryButton, toggleThemeButton;
    private List<String> searchHistory = new ArrayList<>();
    private boolean isDarkMode = false;
    
    //added for favorites button functionality
    private JButton addFavoriteButton, viewFavoritesButton;

    //added for translation functionality
    private JComboBox<TranslationOption> translationBox;
    
    //added to store favorite verses list in memory
    private List<String> favoriteVerses = new ArrayList<>();
    
    public VerseSearchApp() {
        setTitle("VerseSearch - Bible API Viewer");
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeUI();
        pack();
    }

    //added - Class to store translation ID and display name
    private static class TranslationOption {
        private final String id;
        private final String displayName;

        public TranslationOption(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    
    private void initializeUI() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(new EmptyBorder(15, 15, 15, 15));
        container.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel inputLabel = new JLabel("Verse Reference (e.g., John 3:16 or John 3:16-18):");
        inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(inputLabel, gbc);

        verseInput = new JTextField(28);
        verseInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        verseInput.setBorder(new CompoundBorder(new LineBorder(new Color(180, 180, 180)), new EmptyBorder(5, 8, 5, 8)));
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        topPanel.add(verseInput, gbc);
        
        //added - will allow user to press enter to search instead of always having to click the search button
        verseInput.addActionListener(e -> fetchVerse());


        fetchButton = createStyledButton("Search", new Color(33, 150, 243));
        clearButton = createStyledButton("Clear", new Color(244, 67, 54));
        historyButton = createStyledButton("View History", new Color(100, 149, 237));
        saveHistoryButton = createStyledButton("Save History", new Color(100, 149, 237));
        toggleThemeButton = createStyledButton("Toggle Theme", new Color(120, 120, 120));
        
        //added - create favorites buttons
        addFavoriteButton = createStyledButton("Add to Favorites", new Color(76, 175, 80));
        viewFavoritesButton = createStyledButton("View Favorites", new Color(60, 179, 113));


        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0; topPanel.add(fetchButton, gbc);
        gbc.gridx = 1; topPanel.add(clearButton, gbc);
        gbc.gridx = 2; topPanel.add(historyButton, gbc);
        gbc.gridx = 3; topPanel.add(saveHistoryButton, gbc);
        gbc.gridx = 4; topPanel.add(toggleThemeButton, gbc);
        
        
        //added - for selecting the translation 
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel translationLabel = new JLabel("Translation:");
        translationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(translationLabel, gbc);

        //translation options
        TranslationOption[] translations = {
                new TranslationOption("web", "WEB - World English Bible (default)"),
                new TranslationOption("kjv", "KJV - King James Version"),
                new TranslationOption("asv", "ASV - American Standard Version"),
                new TranslationOption("bbe", "BBE - Bible in Basic English")
        };

        gbc.gridx = 1;
        gbc.gridwidth = 4;
        translationBox = new JComboBox<>(translations);
        translationBox.setSelectedIndex(0);
        translationBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(translationBox, gbc);
        
        //added - for favorites button
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        topPanel.add(addFavoriteButton, gbc);

        gbc.gridx = 1;
        topPanel.add(viewFavoritesButton, gbc);
        

        container.add(topPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBorder(new CompoundBorder(new LineBorder(new Color(180, 180, 180)), new EmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)), "Verse Result", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 13)));
        container.add(scrollPane, BorderLayout.CENTER);

        add(container);

        fetchButton.addActionListener(e -> fetchVerse());
        clearButton.addActionListener(e -> outputArea.setText(""));
        historyButton.addActionListener(e -> viewHistory());
        saveHistoryButton.addActionListener(e -> saveHistoryToFile());
        toggleThemeButton.addActionListener(e -> toggleTheme(container));
        
        //added - for favorites button actions
        addFavoriteButton.addActionListener(e -> addCurrentToFavorites());
        viewFavoritesButton.addActionListener(e -> viewFavorites());

    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void toggleTheme(JPanel container) {
        isDarkMode = !isDarkMode;
        Color background = isDarkMode ? new Color(30, 30, 30) : Color.WHITE;
        Color foreground = isDarkMode ? Color.WHITE : Color.BLACK;
        container.setBackground(background);

        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) comp.setBackground(background);
            comp.setForeground(foreground);
        }

        outputArea.setBackground(background);
        outputArea.setForeground(foreground);
        verseInput.setBackground(background);
        verseInput.setForeground(foreground);
        
        //added - for the translation dropdown
        if (translationBox != null) {
            if (isDarkMode) {
                translationBox.setBackground(new Color(60, 60, 60));
                translationBox.setForeground(Color.WHITE);
            } else {
                translationBox.setBackground(Color.WHITE);
                translationBox.setForeground(Color.BLACK);
            }
        }
    }

    private void fetchVerse() {
        String reference = verseInput.getText().trim().replace(" ", "+");
        if (reference.isEmpty()) {
            outputArea.setText("Please enter a verse reference.");
            return;
        }

         //added - for selected translation ID
        TranslationOption selected = (TranslationOption) translationBox.getSelectedItem();
        String translationId = (selected != null) ? selected.getId() : "web";

        try {
        String apiUrl = "https://bible-api.com/" + reference + "?translation=" + translationId;
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String result = parseVerseResponse(response.toString());
                outputArea.setText(result);
                //modified here to add translation ID
                searchHistory.add(reference.replace("+", " ") + " [" + translationId.toUpperCase() + "]:\n" + result);

            } else {
                outputArea.setText("Verse not found or API error.\nResponse Code: " + responseCode);
            }

        } catch (Exception e) {
            outputArea.setText("Error fetching verse: " + e.getMessage());
        }
    }

    private String parseVerseResponse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("verses")) {
                JSONArray verses = obj.getJSONArray("verses");
                StringBuilder result = new StringBuilder();

                for (int i = 0; i < verses.length(); i++) {
                    JSONObject verse = verses.getJSONObject(i);
                    String reference = verse.getString("book_name") + " " +
                            verse.getInt("chapter") + ":" +
                            verse.getInt("verse");
                    String text = verse.getString("text").trim();
                    result.append(reference).append(" - ").append(text).append("\n\n");
                }
                return result.toString();
            } else {
                return parseSingleVerse(json);
            }
        } catch (Exception e) {
            return "Error parsing verses: " + e.getMessage();
        }
    }

    private String parseSingleVerse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String reference = obj.getString("reference");
            String text = obj.getString("text").trim();
            return reference + " - " + text;
        } catch (Exception e) {
            return "Error parsing single verse: " + e.getMessage();
        }
    }

    private void viewHistory() {
        if (searchHistory.isEmpty()) {
            outputArea.setText("No search history available.");
        } else {
            StringBuilder historyText = new StringBuilder("Search History:\n\n");
            for (String entry : searchHistory) {
                historyText.append(entry).append("\n-----------------------------\n");
            }
            outputArea.setText(historyText.toString());
        }
    }

    private void saveHistoryToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("verse_search_history.txt", true))) {
            for (String entry : searchHistory) {
                writer.println(entry);
                writer.println("-----------------------------");
            }
            outputArea.setText("Search history saved to verse_search_history.txt");
        } catch (IOException e) {
            outputArea.setText("Error saving history: " + e.getMessage());
        }
    }
    
    //added- method to add current verse to favoties list
    private void addCurrentToFavorites() {
        String text = outputArea.getText().trim();
        if (text.isEmpty() || text.startsWith("Please enter a verse reference.") || text.startsWith("Error") || text.startsWith("Verse not found") || text.startsWith("No search history")) {
            JOptionPane.showMessageDialog(this,
                    "There is no verse result to add to favorites.",
                    "No Verse",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        favoriteVerses.add(text);
        JOptionPane.showMessageDialog(this,
                "Current verse(s) added to favorites.",
                "Added to Favorites",
                JOptionPane.INFORMATION_MESSAGE);
    }

    //added - to show all the favorite verses in the favorites list
    private void viewFavorites() {
        if (favoriteVerses.isEmpty()) {
            outputArea.setText("No favorite verses have been added yet.");
        } else {
            StringBuilder sb = new StringBuilder("Favorite Verses:\n\n");
            for (String fav : favoriteVerses) {
                sb.append(fav).append("\n-----------------------------\n");
            }  
            outputArea.setText(sb.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerseSearchApp().setVisible(true));
    }
}

Add translation selector and favorites
