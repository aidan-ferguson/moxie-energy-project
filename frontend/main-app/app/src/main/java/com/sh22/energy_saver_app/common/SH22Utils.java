package com.sh22.energy_saver_app.common;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.backend.AuthenticationException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    // Method to get a view from the backend
    public static String getBackendView(String view_url, HashMap<String, String> requestProperties) throws AuthenticationException, IOException {
        String url_str = Constants.SERVER_BASE_URL + view_url;
        URL url;
        try {
            url = new URL(url_str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IOException();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (requestProperties != null) {
            for(String key : requestProperties.keySet()) {
                connection.addRequestProperty(key, requestProperties.get(key));
            }
        }
        connection.connect();

        int response_code = connection.getResponseCode();
        if (response_code != 200) {
            String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
            Log.e("sh22", "BackendInterface::GetNationalAverages server returned code " + response_code);
            Log.e("moxie", "BackendInterface::GetNationalAverages server returned: " + error_reason);
            connection.disconnect();
            throw new IOException();
        } else {
            return SH22Utils.readFullStream(connection.getInputStream());
        }
    }
}
