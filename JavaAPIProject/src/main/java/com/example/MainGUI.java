package com.example;

import java.awt.*;
import javax.swing.*;

public class MainGUI {
    private static final String API_KEY = "1143d463ff4fd46586b548ef06f1223e";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Globe Weather Viewer");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel latLabel = new JLabel("Latitude:");
        JTextField latField = new JTextField();
        JLabel lonLabel = new JLabel("Longitude:");
        JTextField lonField = new JTextField();
        JLabel dateLabel = new JLabel("Date (MM-DD):");
        JTextField dateField = new JTextField();
        JButton fetchButton = new JButton("Get Average Weather");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(7, 1));
        panel.add(latLabel); panel.add(latField);
        panel.add(lonLabel); panel.add(lonField);
        panel.add(dateLabel); panel.add(dateField);
        panel.add(fetchButton);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        fetchButton.addActionListener(e -> {
            try {
                double lat = Double.parseDouble(latField.getText());
                double lon = Double.parseDouble(lonField.getText());
                String[] date = dateField.getText().split("-");
                int month = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);

                double[] temps = new double[20]; 
                for (int i = 0; i < 20; i++) {
                    int year = 2024 - i;
                    long unix = getUnixTimestamp(year, month, day);
                    String json = WeatherAPI.getWeatherData(lat, lon, unix, API_KEY);

                    
                }

                double avgTemp = AverageCalculator.getAverage(temps);
                resultArea.setText("Average Temp (last 20 years): " + avgTemp + " °C");

            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    private static long getUnixTimestamp(int year, int month, int day) {
        return java.time.LocalDate.of(year, month, day)
            .atStartOfDay(java.time.ZoneOffset.UTC)
            .toEpochSecond();
    }
}