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

    public VerseSearchApp() {
        setTitle("VerseSearch - Bible API Viewer");
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initializeUI();
        pack();
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

        fetchButton = createStyledButton("Search", new Color(33, 150, 243));
        clearButton = createStyledButton("Clear", new Color(244, 67, 54));
        historyButton = createStyledButton("View History", new Color(100, 149, 237));
        saveHistoryButton = createStyledButton("Save History", new Color(100, 149, 237));
        toggleThemeButton = createStyledButton("Toggle Theme", new Color(120, 120, 120));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0; topPanel.add(fetchButton, gbc);
        gbc.gridx = 1; topPanel.add(clearButton, gbc);
        gbc.gridx = 2; topPanel.add(historyButton, gbc);
        gbc.gridx = 3; topPanel.add(saveHistoryButton, gbc);
        gbc.gridx = 4; topPanel.add(toggleThemeButton, gbc);

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
    }

    private void fetchVerse() {
        String reference = verseInput.getText().trim().replace(" ", "+");
        if (reference.isEmpty()) {
            outputArea.setText("Please enter a verse reference.");
            return;
        }

        try {
            URL url = new URL("https://bible-api.com/" + reference);
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
                searchHistory.add(reference.replace("+", " ") + ":\n" + result);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerseSearchApp().setVisible(true));
    }
}
