package com.example;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.WorldMapLayer;

import javax.swing.*;

import org.json.JSONObject;

import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class GlobeWeatherApp {

    private static final String API_KEY = "1143d463ff4fd46586b548ef06f1223e";

    public static void main(String[] args) {
        final WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        wwd.setPreferredSize(new Dimension(800, 600));
        wwd.getModel().getLayers().add(new WorldMapLayer());

        JFrame frame = new JFrame("Click Globe for Weather");
        frame.getContentPane().add(wwd, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        wwd.addSelectListener(new SelectListener() {
            @Override
            public void selected(SelectEvent event) {
                if (event.getEventAction().equals(SelectEvent.LEFT_CLICK) && event.getTopPickedObject() == null)  
                {
                    Point clickPoint = event.getPickPoint();
                    Position pos = wwd.getView()
                                      .computePositionFromScreenPoint(clickPoint.x, clickPoint.y);
                    if (pos != null) {
                        double lat = pos.getLatitude().degrees;
                        double lon = pos.getLongitude().degrees;
                        requestAndShowWeather(lat, lon);
                    }
                }
            }
        });
    }

    private static void requestAndShowWeather(double lat, double lon) {
        String input = JOptionPane.showInputDialog(
            null,
            "Enter date (MM-DD):",
            "Date Input",
            JOptionPane.QUESTION_MESSAGE
        );
        if (input == null || !input.matches("\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(null, "Invalid format.");
            return;
        }
        String[] parts = input.split("-");
        int month = Integer.parseInt(parts[0]), day = Integer.parseInt(parts[1]);


        double[] temps = new double[20], precs = new double[20];
        for (int i = 0; i < 20; i++) {
            int year = LocalDate.now().getYear() - i;
            long unix = LocalDate.of(year, month, day)
                                 .atStartOfDay(ZoneOffset.UTC).toEpochSecond();
            try {
                String json = WeatherAPI.getWeatherData(lat, lon, unix, API_KEY);
                JSONObject cur = new JSONObject(json).getJSONObject("current");

                temps[i] = cur.getDouble("temp");
                double p = 0;
                if (cur.has("rain"))   p = cur.getJSONObject("rain").optDouble("1h", 0);
                else if (cur.has("snow")) p = cur.getJSONObject("snow").optDouble("1h", 0);
                precs[i] = p;

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "API error: " + ex.getMessage());
                return;
            }
        }

        double avgT = AverageCalculator.getAverage(temps);
        double avgP = AverageCalculator.getAverage(precs);
        JOptionPane.showMessageDialog(
            null,
            String.format(
              "Location: (%.4f, %.4f)\nDate: %s\n\n" +
              "Avg Temp (°C): %.2f\nAvg Precip (mm): %.2f",
              lat, lon, input, avgT, avgP
            )
        );
    }
}