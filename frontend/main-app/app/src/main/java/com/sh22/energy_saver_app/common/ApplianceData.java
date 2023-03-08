package com.sh22.energy_saver_app.common;

import java.util.ArrayList;

public class ApplianceData {
    public float energy_score = 0.0f;
    public ArrayList<String> labels = new ArrayList<String>();
    public ArrayList<Double> initial_usage = new ArrayList<Double>();
    public ArrayList<Double> weekly_average = new ArrayList<Double>();
    public ArrayList<Double> today = new ArrayList<Double>();
    public ArrayList<Double> national_averages = new ArrayList<Double>();
}

