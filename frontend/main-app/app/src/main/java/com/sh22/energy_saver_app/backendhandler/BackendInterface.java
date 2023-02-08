package com.sh22.energy_saver_app.backendhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
            JSONArray json_previous_week = json_data.getJSONArray("previous_week");
            JSONArray json_current_day = json_data.getJSONArray("today");
            ApplianceData applianceData = new ApplianceData();
            for (int i = 0; i < json_labels.length(); i++) {
                applianceData.labels.add(json_labels.getString(i));
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
}
