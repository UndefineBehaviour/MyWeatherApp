package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * A modern-looking Swing weather app.
 * - Search any city in India or worldwide (type "City" or "City,CountryCode")
 * - Quick-access buttons for popular Indian cities
 * - Shows temperature, condition, feels-like, min/max, humidity, wind,
 *   pressure, visibility, sunrise and sunset
 * - Light / Dark theme toggle
 */
public class WeatherAppGUI extends JFrame {

    // ---- Theme colors ----
    private boolean darkMode = false;

    private static final Color LIGHT_BG = new Color(244, 247, 250);
    private static final Color LIGHT_CARD = Color.WHITE;
    private static final Color LIGHT_TEXT = new Color(33, 37, 41);
    private static final Color LIGHT_SUBTEXT = new Color(108, 117, 125);

    private static final Color DARK_BG = new Color(24, 26, 36);
    private static final Color DARK_CARD = new Color(38, 41, 58);
    private static final Color DARK_TEXT = new Color(235, 237, 240);
    private static final Color DARK_SUBTEXT = new Color(160, 165, 180);

    private static final Color ACCENT = new Color(90, 120, 245);

    // ---- Components that need re-theming ----
    private JPanel rootPanel;
    private JPanel headerPanel;
    private JPanel searchPanel;
    private JPanel quickCitiesPanel;
    private JPanel mainCardPanel;
    private JPanel detailsGridPanel;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JTextField cityField;
    private JButton searchButton;
    private JButton themeToggleButton;
    private JLabel cityNameLabel;
    private JLabel tempLabel;
    private JLabel conditionLabel;
    private WeatherIconPanel iconPanel;
    private final List<JPanel> detailCards = new ArrayList<>();
    private final List<JLabel> detailTitleLabels = new ArrayList<>();
    private final List<JLabel> detailValueLabels = new ArrayList<>();
    private final List<JButton> quickCityButtons = new ArrayList<>();

    private static final String[] QUICK_CITIES = {
            "Thane", "Mumbai", "Delhi", "Bengaluru", "Pune", "Chennai", "Kolkata", "Hyderabad"
    };

    public WeatherAppGUI() {
        super("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 720);
        setMinimumSize(new Dimension(420, 640));
        setLocationRelativeTo(null);

        buildUI();
        applyTheme();

        setVisible(true);

        // Load a default city on startup
        fetchAndDisplay("Thane,IN");
    }

    private void buildUI() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
        rootPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(rootPanel);

        // ---------- Header ----------
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        titleLabel = new JLabel("Weather");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        themeToggleButton = new JButton("Dark Mode");
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.addActionListener(this::onToggleTheme);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(themeToggleButton, BorderLayout.EAST);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(headerPanel);
        rootPanel.add(Box.createVerticalStrut(16));

        // ---------- Search ----------
        searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setOpaque(false);
        cityField = new JTextField();
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 210), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        cityField.setToolTipText("Type any city worldwide, e.g. \"Thane\" or \"Paris,FR\"");
        cityField.addActionListener(this::onSearch);

        searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);
        searchButton.setBackground(ACCENT);
        searchButton.setForeground(Color.BLACK);
        searchButton.addActionListener(this::onSearch);

        searchPanel.add(cityField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        rootPanel.add(searchPanel);
        rootPanel.add(Box.createVerticalStrut(10));

        // ---------- Quick city buttons ----------
        quickCitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        quickCitiesPanel.setOpaque(false);
        for (String city : QUICK_CITIES) {
            JButton b = new JButton(city);
            b.setFocusPainted(false);
            b.setFont(new Font("SansSerif", Font.PLAIN, 12));
            b.addActionListener(e -> {
                cityField.setText(city);
                fetchAndDisplay(city + ",IN");
            });
            quickCityButtons.add(b);
            quickCitiesPanel.add(b);
        }
        quickCitiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(quickCitiesPanel);
        rootPanel.add(Box.createVerticalStrut(6));

        // ---------- Status label (errors / loading) ----------
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(statusLabel);
        rootPanel.add(Box.createVerticalStrut(10));

        // ---------- Main weather card ----------
        mainCardPanel = new JPanel();
        mainCardPanel.setLayout(new BoxLayout(mainCardPanel, BoxLayout.Y_AXIS));
        mainCardPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        mainCardPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainCardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        cityNameLabel = new JLabel("--");
        cityNameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        cityNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        iconPanel = new WeatherIconPanel();
        iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        tempLabel = new JLabel("--°C");
        tempLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        conditionLabel = new JLabel("--");
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainCardPanel.add(cityNameLabel);
        mainCardPanel.add(Box.createVerticalStrut(6));
        mainCardPanel.add(iconPanel);
        mainCardPanel.add(tempLabel);
        mainCardPanel.add(conditionLabel);

        rootPanel.add(mainCardPanel);
        rootPanel.add(Box.createVerticalStrut(14));

        // ---------- Details grid ----------
        detailsGridPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        detailsGridPanel.setOpaque(false);
        detailsGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] titles = {
                "Feels Like", "Humidity", "Wind Speed", "Pressure",
                "Min / Max Temp", "Visibility", "Sunrise", "Sunset"
        };
        for (String t : titles) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(14, 16, 14, 16));

            JLabel titleLbl = new JLabel(t);
            titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel valueLbl = new JLabel("--");
            valueLbl.setFont(new Font("SansSerif", Font.BOLD, 17));
            valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(titleLbl);
            card.add(Box.createVerticalStrut(4));
            card.add(valueLbl);

            detailCards.add(card);
            detailTitleLabels.add(titleLbl);
            detailValueLabels.add(valueLbl);
            detailsGridPanel.add(card);
        }

        rootPanel.add(detailsGridPanel);
        rootPanel.add(Box.createVerticalGlue());
    }

    private void onSearch(ActionEvent e) {
        String city = cityField.getText();
        if (city == null || city.isBlank()) {
            statusLabel.setForeground(new Color(211, 47, 47));
            statusLabel.setText("Please type a city name.");
            return;
        }
        fetchAndDisplay(city.trim());
    }

    private void onToggleTheme(ActionEvent e) {
        darkMode = !darkMode;
        themeToggleButton.setText(darkMode ? "Light Mode" : "Dark Mode");
        applyTheme();
    }

    /** Runs the API call off the EDT so the UI never freezes. */
    private void fetchAndDisplay(String cityQuery) {
        searchButton.setEnabled(false);
        statusLabel.setForeground(darkMode ? DARK_SUBTEXT : LIGHT_SUBTEXT);
        statusLabel.setText("Loading weather for \"" + cityQuery + "\"...");

        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                return WeatherApiClient.fetchWeatherData(cityQuery);
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                try {
                    WeatherData data = get();
                    displayWeather(data);
                    statusLabel.setText(" ");
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setForeground(new Color(211, 47, 47));
                    statusLabel.setText(cause.getMessage() != null ? cause.getMessage() : "Something went wrong.");
                }
            }
        };
        worker.execute();
    }

    private void displayWeather(WeatherData data) {
        String location = data.getCityName() + (data.getCountry().isEmpty() ? "" : ", " + data.getCountry());
        cityNameLabel.setText(location);
        tempLabel.setText(Math.round(data.getTemperature()) + "°C");
        String condition = data.getWeatherDescription() == null ? "" : data.getWeatherDescription();
        conditionLabel.setText(capitalize(condition));
        iconPanel.setCondition(data.getWeatherMain());

        setDetailValue(0, Math.round(data.getFeelsLike()) + "°C");
        setDetailValue(1, data.getHumidity() + "%");
        setDetailValue(2, String.format("%.1f km/h", data.getWindSpeedKmh()));
        setDetailValue(3, data.getPressure() + " hPa");
        setDetailValue(4, Math.round(data.getTempMin()) + "° / " + Math.round(data.getTempMax()) + "°C");
        setDetailValue(5, String.format("%.1f km", data.getVisibilityKm()));
        setDetailValue(6, formatLocalTime(data.getSunriseEpoch(), data.getTimezoneOffsetSeconds()));
        setDetailValue(7, formatLocalTime(data.getSunsetEpoch(), data.getTimezoneOffsetSeconds()));
    }

    private void setDetailValue(int index, String value) {
        detailValueLabels.get(index).setText(value);
    }

    private String formatLocalTime(long epochSeconds, int timezoneOffsetSeconds) {
        if (epochSeconds <= 0) return "--";
        ZonedDateTime zdt = Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneOffset.ofTotalSeconds(timezoneOffsetSeconds));
        return zdt.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ---------------- Theming ----------------

    private void applyTheme() {
        Color bg = darkMode ? DARK_BG : LIGHT_BG;
        Color card = darkMode ? DARK_CARD : LIGHT_CARD;
        Color text = darkMode ? DARK_TEXT : LIGHT_TEXT;
        Color subtext = darkMode ? DARK_SUBTEXT : LIGHT_SUBTEXT;

        rootPanel.setBackground(bg);
        titleLabel.setForeground(text);
        cityField.setBackground(card);
        cityField.setForeground(text);
        cityField.setCaretColor(text);

        mainCardPanel.setBackground(card);
        mainCardPanel.setOpaque(true);
        cityNameLabel.setForeground(text);
        tempLabel.setForeground(ACCENT);
        conditionLabel.setForeground(subtext);

        statusLabel.setForeground(subtext);

        for (int i = 0; i < detailCards.size(); i++) {
            detailCards.get(i).setBackground(card);
            detailCards.get(i).setOpaque(true);
            detailTitleLabels.get(i).setForeground(subtext);
            detailValueLabels.get(i).setForeground(text);
        }

        for (JButton b : quickCityButtons) {
            b.setBackground(card);
            b.setForeground(text);
        }

        themeToggleButton.setBackground(card);
        themeToggleButton.setForeground(text);

        getContentPane().setBackground(bg);
        repaint();
    }

    public static void main(String[] args) {
        // Use the system look and feel where available for a native feel.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fall back to the default cross-platform look and feel.
        }
        SwingUtilities.invokeLater(WeatherAppGUI::new);
    }
}
