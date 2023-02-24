package com.sh22.energy_saver_app.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.tv.interactive.AppLinkInfo;
import android.util.Log;
import android.widget.Toast;

import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.ui.activites.LoginActivity;
import com.sh22.energy_saver_app.ui.activites.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SH22Utils {
    // Variable to hold the one error toast for the application
    public static Toast error_toast = null;

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

    // Calculate and return a normalised energy score for a given device that compares initial usage vs today
    public static float getEnergyScore(ArrayList<ApplianceCardData> data, int index) {
        if(index < 0){
            return 0.0f;
        }
        return SH22Utils.normaliseEnergyRating((float)(data.get(index).getInitialUsage()/data.get(index).getUsageToday()));
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
        reader.close();
        return buffer.toString();
    }

    // Method to get a view from the backend
    public static String getBackendView(String view_url, HashMap<String, String> requestProperties) throws AuthenticationException, IOException, BackendException {
        String url_str = Constants.SERVER_BASE_URL + view_url;
        URL url;
        try {
            url = new URL(url_str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IOException();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(Constants.SERVER_CONNECT_TIMEOUT);
        if (requestProperties != null) {
            for(String key : requestProperties.keySet()) {
                connection.addRequestProperty(key, requestProperties.get(key));
            }
        }
        connection.connect();

        int response_code = connection.getResponseCode();
        if (response_code != 200) {
            connection.disconnect();

            if(response_code == 401) {
                throw new AuthenticationException();
            }

            // Error occurred, attempt to get reason and pass it back up
            String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
            Log.e("sh22", "SH22Utils::getBackendView server returned code " + response_code);
            Log.e("moxie", "SH22Utils::getBackendView server returned: " + error_reason);

            // Attempt to get json error if it exists
            try {
                throw new BackendException(new JSONObject(error_reason).getString("reason"));
            } catch (JSONException e) {
                throw new BackendException("An internal server error occurred");
            }
        } else {
            return SH22Utils.readFullStream(connection.getInputStream());
        }
    }

    // Method to POST to a backend view
    public static String postBackendView(String view_url, HashMap<String, String> requestProperties, String form_data) throws AuthenticationException, IOException {
        String url_str = Constants.SERVER_BASE_URL + view_url;
        URL url;
        try {
            url = new URL(url_str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IOException();
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(Constants.SERVER_CONNECT_TIMEOUT);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (requestProperties != null) {
            for(String key : requestProperties.keySet()) {
                connection.addRequestProperty(key, requestProperties.get(key));
            }
        }

        try( DataOutputStream writer = new DataOutputStream( connection.getOutputStream())) {
            writer.write(form_data.getBytes(StandardCharsets.UTF_8));
        }
        connection.connect();

        int response_code = connection.getResponseCode();
        if (response_code != 200) {
            connection.disconnect();
            if(response_code == 401) {
                throw new AuthenticationException();
            }

            String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
            Log.e("sh22", "SH22Utils::getBackendView server returned code " + response_code);
            Log.e("moxie", "SH22Utils::getBackendView server returned: " + error_reason);

            throw new IOException();
        } else {
            return SH22Utils.readFullStream(connection.getInputStream());
        }
    }

    // TODO: logout on backend
    public static void Logout(Context context) {
        // Erase tokens
        AuthenticationHandler.Logout(context);

        // Replace the activity
        Activity main_activity = (MainActivity)context;
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        main_activity.finish(); // Disable user from going back
    }

    // Will create a toast when any backend exception occurs
    public static void ToastException(Context context, String error) {
        ((Activity)context).runOnUiThread(() -> {
            // Ensure only one toast is shown even if multiple errors occur
            if(SH22Utils.error_toast != null) {
                SH22Utils.error_toast.cancel();
            }
            SH22Utils.error_toast = Toast.makeText(context, error, Toast.LENGTH_LONG);
            error_toast.show();
        });
    }
}
