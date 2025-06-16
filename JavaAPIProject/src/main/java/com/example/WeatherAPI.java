package com.example;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherAPI {
    public static double getDayTemperature(double lat, double lon, String apiKey) throws Exception {
        String urlStr = String.format(
            "https://history.openweathermap.org/data/2.5/history/city?lat="+lat+"&lon="+lon+"&appid="+apiKey);
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
        //parse json and collect neccecary info 
        String jsonString = response.toString();
        JSONObject obj = new JSONObject(jsonString);
        JSONArray list = obj.getJSONArray("list");
        JSONObject item = list.getJSONObject(0);
        item = item.getJSONObject("main");

        double temperature = item.getDouble("temp");

       return temperature - 273.15;


    }
}