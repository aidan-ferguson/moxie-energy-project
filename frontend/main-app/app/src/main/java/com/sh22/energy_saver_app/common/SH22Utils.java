package com.sh22.energy_saver_app.common;

public class SH22Utils {

    // Sigmoid function
    private static float sigmoid(float x) {
        return (float)(1/(1+Math.exp(-x)));
    }

    // Will return a normalised energy rating between 0-100
    // https://www.desmos.com/calculator/4xkdff7daf
    public static float normaliseEnergyRating(float rating) {
        float a = 20;
        return 100 * sigmoid((1/a) * (rating - (a * 5)));
    }
}
