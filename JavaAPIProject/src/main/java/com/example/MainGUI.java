package com.example;

import java.awt.*;
import javax.swing.*;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.net.*;
import java.io.*;
import org.json.*;

public class MainGUI {
    private static final String API_KEY = "1143d463ff4fd46586b548ef06f1223e";

    public static void main(String[] args) {
        //create field for text box and button

        JFrame frame = new JFrame("Weather for One Day");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel placeLabel = new JLabel("City/Country:");
        JTextField placeField = new JTextField();
        JButton fetchButton = new JButton("Get Temp");
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(4, 1));
        panel.add(placeLabel); panel.add(placeField);
        //panel.add(dateLabel); panel.add(dateField);
        panel.add(fetchButton);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        fetchButton.addActionListener(e -> {
            try {
                //convert name  of the city to lat and long 
                String place = URLEncoder.encode(placeField.getText(), "UTF-8");
                String geoUrl = "http://api.openweathermap.org/geo/1.0/direct?q=" + place + "&limit=1&appid=" + API_KEY;

                String geoJson = readUrl(geoUrl);
                JSONArray geoArray = new JSONArray(geoJson);
                if (geoArray.isEmpty()) {
                    resultArea.setText("Location not found.");
                    return;
                }
                JSONObject location = geoArray.getJSONObject(0);
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");

                //String[] dateParts = dateField.getText().split("-");
                //LocalDate date = LocalDate.of(2024, Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]));
                //long unix = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();

                double temp = WeatherAPI.getDayTemperature(lat, lon, API_KEY);
                if (!Double.isNaN(temp)) {
                    resultArea.setText("Temperature for " + location.getString("name") + " is :\n" +
                                       String.format("%.2f °C", temp));
                } else {
                    resultArea.setText("No data available for that date.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                resultArea.setText("Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    private static String readUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) result.append(line);
        reader.close();
        return result.toString();
    }
}