package com.sh22.energy_saver_app.common;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SH22Utils {

    // Sigmoid function
    private static float sigmoid(float x) {
        return (float)(1/(1+Math.exp(-x * Constants.ENERGY_SIGMOID_GRADIANT_STEEPNESS)));
    }

    // Will return a normalised energy rating in the range (0, 1)
    // https://www.desmos.com/calculator/mifnjmnoy4
    public static float normaliseEnergyRating(float rating) {
        float a = Constants.ENERGY_NORMALISATION_SIGMOID_STRECH;
        return sigmoid((1/a) * (rating - (a * 5)));
    }

    // Calculate and return a normalised energy score for a given device that compares today with the past week
    public static float getEnergyScore(ApplianceData data, String label) {
        int index = data.labels.indexOf(label);
        if(index < 0){
            return 0.0f;
        }
        return SH22Utils.normaliseEnergyRating((float)(data.weekly_average.get(index)/data.today.get(index)));
    }

    // Convert dp units to pixels for this device
    public static int dpToPixels(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    // Utility function to read a full input stream into a string
    public static String readFullStream(InputStream stream) throws IOException {
        InputStreamReader inputStream = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder buffer = new StringBuilder();
        String line;
        while((line = reader.readLine())!= null){
            buffer.append(line);
        }
        return buffer.toString();
    }
}
