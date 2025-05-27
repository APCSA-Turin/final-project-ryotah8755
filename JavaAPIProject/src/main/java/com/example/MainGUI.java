package com.example;

import java.awt.*;
import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainGUI {
    private static final String API_KEY = "1143d463ff4fd46586b548ef06f1223e"; // Replace with your real API key

    public static void main(String[] args) {
        JFrame frame = new JFrame("Globe Weather Viewer");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel latLabel = new JLabel("Latitude:");
        JTextField latField = new JTextField();
        JLabel lonLabel = new JLabel("Longitude:");
        JTextField lonField = new JTextField();
        JLabel datestartLabel = new JLabel("Start Date (MM-DD):");
        JTextField datestartField = new JTextField();
        JLabel dateendLabel = new JLabel("End Date (MM-DD):");
        JTextField dateendField = new JTextField();
        JButton fetchButton = new JButton("Get Average Weather");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.add(latLabel); panel.add(latField);
        panel.add(lonLabel); panel.add(lonField);
        panel.add(datestartLabel); panel.add(datestartField);
        panel.add(dateendLabel); panel.add(dateendField);
        panel.add(fetchButton);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        fetchButton.addActionListener(e -> {
            try {
                double lat = Double.parseDouble(latField.getText());
                double lon = Double.parseDouble(lonField.getText());

                String[] datestart = datestartField.getText().split("-");
                String[] dateend = dateendField.getText().split("-");
                int startMonth = Integer.parseInt(datestart[0]);
                int startDay = Integer.parseInt(datestart[1]);
                int endMonth = Integer.parseInt(dateend[0]);
                int endDay = Integer.parseInt(dateend[1]);

                LocalDate startDate = LocalDate.of(2024, startMonth, startDay);
                LocalDate endDate = LocalDate.of(2024, endMonth, endDay);

                double tempSum = 0;
                int count = 0;

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    long unix = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
                    String json = WeatherAPI.getWeatherData(lat, lon, unix, API_KEY);

                    JSONObject root = new JSONObject(json);
                    JSONArray hourly = root.getJSONArray("data");

                    for (int i = 0; i < hourly.length(); i++) {
                        JSONObject hourData = hourly.getJSONObject(i);
                        if (hourData.has("temp")) {
                            tempSum += hourData.getDouble("temp");
                            count++;
                        }
                    }
                }

                if (count > 0) {
                    double avgTemp = tempSum / count;
                    resultArea.setText("Average Temp from " + startDate + " to " + endDate + ":\n"
                            + String.format("%.2f", avgTemp) + " °C");
                } else {
                    resultArea.setText("No temperature data found for that range.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }
}