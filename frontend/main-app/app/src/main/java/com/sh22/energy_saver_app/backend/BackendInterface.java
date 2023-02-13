package com.sh22.energy_saver_app.backend;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.common.UserInfo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackendInterface {
    private static final String appliance_url_str = "http://10.0.2.2:8000/api/usage/appliances";
    private static ApplianceData cached_data = null;
    static Object lock = new Object();

        // Function to get the past weeks energy usage and todays energy usage per device
    public static ApplianceData get_appliance_data() throws IOException, JSONException {
        // Just for debugging
//        ApplianceData applianceDataDebug = new ApplianceData();
//        applianceDataDebug.labels.add("aggregate");
//        applianceDataDebug.labels.add("kettle");
//        applianceDataDebug.labels.add("freezer");
//        applianceDataDebug.labels.add("lights");
//        applianceDataDebug.weekly_average.add(286.72630231842936);
//        applianceDataDebug.weekly_average.add(13.089364192815548);
//        applianceDataDebug.weekly_average.add(33.90864177934743);
//        applianceDataDebug.weekly_average.add(5.90864177934743);
//        applianceDataDebug.today.add(351.7993889313242);
//        applianceDataDebug.today.add(21.88229984028887);
//        applianceDataDebug.today.add(39.5259356989098);
//        applianceDataDebug.today.add(4.90864177934743);
//        cached_data = applianceDataDebug;

        synchronized (lock) {
            if (cached_data != null) {
                return cached_data;
            }

            URL apiURL = null;
            try {
                apiURL = new URL(appliance_url_str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            // Create HTTP connection (we don't have SSL setup on django)
            URLConnection connection = apiURL.openConnection();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            // TODO: handle http errors
            Scanner s = new Scanner(reader).useDelimiter("\\A");
            String data = s.hasNext() ? s.next() : "";

            Log.d("moxie", data);

            JSONObject json_data = new JSONObject(data).getJSONObject("data");
            JSONArray json_labels = json_data.getJSONArray("labels");
            JSONArray json_initial_usage = json_data.getJSONArray("initial_usage");
            JSONArray json_previous_week = json_data.getJSONArray("previous_week");
            JSONArray json_current_day = json_data.getJSONArray("today");
            ApplianceData applianceData = new ApplianceData();
            for (int i = 0; i < json_labels.length(); i++) {
                applianceData.labels.add(json_labels.getString(i));
            }
            for (int i = 0; i < json_initial_usage.length(); i++) {
                applianceData.initial_usage.add(json_initial_usage.getDouble(i));
            }
            for (int i = 0; i < json_previous_week.length(); i++) {
                applianceData.weekly_average.add(json_previous_week.getDouble(i));
            }
            for (int i = 0; i < json_current_day.length(); i++) {
                applianceData.today.add(json_current_day.getDouble(i));
            }

            connection.getInputStream().close();
            reader.close();

            cached_data = applianceData;
            lock.notifyAll();
        }
        return cached_data;
    }

    // Method to get the national averages from the database
    public static Map<String, Double> GetNationalAverages() {
        String url_str = Constants.SERVER_BASE_URL + "/usage/national-average";
        URL url = null;
        try { url = new URL(url_str); }
        catch (MalformedURLException e) {e.printStackTrace(); return null;}

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int response_code = connection.getResponseCode();
            if (response_code != 200) {
                String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
                Log.e("sh22", "BackendInterface::GetNationalAverages server returned code " + response_code);
                Log.e("moxie", "BackendInterface::GetNationalAverages server returned: " + error_reason);
                connection.disconnect();
                return null;
            } else {
                // Successful authentication, store and return true
                Map<String, Double> averages = new HashMap<>();
                String response = SH22Utils.readFullStream(connection.getInputStream());
                JSONObject json_data = new JSONObject(response);
                for (Iterator<String> it = json_data.keys(); it.hasNext(); ) {
                    String key = it.next();

                    averages.put(key, json_data.getDouble(key));
                }
                return averages;
            }
        } catch (IOException | JSONException e) { e.printStackTrace(); return null;}
    }

    public static UserInfo GetUserInfo(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

        // Attempt to connect to endpoint and authenticate
        String url_str = Constants.SERVER_BASE_URL + "/user/information";
        URL url = null;
        try { url = new URL(url_str); }
        catch (MalformedURLException e) {e.printStackTrace(); return null;}

        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.connect();

            // If the response code is anything but success, get the error string and return false
            int response_code = connection.getResponseCode();
            if (response_code != 200) {
                String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
                Log.e("sh22", "BackendInterface::GetUserInfo server returned code " + response_code);
                Log.e("moxie", "BackendInterface::GetUserInfo server returned: " + error_reason);
                connection.disconnect();
                return null;
            } else {
                // Successful authentication, store and return true
                String response = SH22Utils.readFullStream(connection.getInputStream());
                JSONObject json_data = new JSONObject(response).getJSONObject("user_data");
                UserInfo userInfo = new UserInfo(
                        json_data.getString("username"),
                        json_data.getString("firstname"),
                        json_data.getString("surname"));
                connection.disconnect();
                return userInfo;
            }
        } catch (IOException | JSONException e) { e.printStackTrace(); return null;}
    }
}
