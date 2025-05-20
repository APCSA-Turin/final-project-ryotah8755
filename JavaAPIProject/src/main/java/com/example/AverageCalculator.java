package com.example;

import org.json.JSONObject;
import java.net.*;
import java.io.*;

public class AverageCalculator {
    public static double getAverage(double[] values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }
}