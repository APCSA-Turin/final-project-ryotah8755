package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {
    public static double getDayTemperature(double lat, double lon, long unixTime, String apiKey) throws Exception {
        String urlStr = String.format(
            "https://api.openweathermap.org/data/3.0/onecall/timemachine?lat=%f&lon=%f&dt=%d&appid=%s&units=metric",
            lat, lon, unixTime, apiKey);
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        org.json.JSONObject json = new org.json.JSONObject(response.toString());
        org.json.JSONArray hourly = json.getJSONArray("hourly");

        double total = 0;
        int count = 0;
        for (int i = 0; i < hourly.length(); i++) {
            total += hourly.getJSONObject(i).getDouble("temp");
            count++;
        }

        return count > 0 ? total / count : Double.NaN;
    }
}