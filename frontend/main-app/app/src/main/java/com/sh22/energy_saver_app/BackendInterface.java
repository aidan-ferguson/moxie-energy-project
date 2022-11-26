package com.sh22.energy_saver_app;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class BackendInterface {
    private static final String url_str = "http://10.0.2.2:8000/api/users/1234/usage/electricity";

    // Function to get the average daily energy usage per device
    public static String get_device_energy_uasge() throws IOException {
        URL apiURL = null;
        try {
            apiURL = new URL(url_str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new String("");
        }

        // Create HTTP connection (we don't have SSL setup on django)
        URLConnection connection = apiURL.openConnection();
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        HashMap<String, Float> daily_energy_readings = new HashMap<String, Float>();
        InputStreamReader responseReader = new InputStreamReader(connection.getInputStream());
        JsonReader jsonReader = new JsonReader(responseReader);
        ArrayList<String> labels = new ArrayList<String>();

        // Just for example return the date and reading of the first entry
        jsonReader.beginObject();
        while(jsonReader.hasNext()) {
            String key = jsonReader.nextName();
            if(key.equals("data")) {
                // Data
                jsonReader.beginObject();
                String key_2 = jsonReader.nextName();
                if(key_2.equals("usage")) {
                    // Enter usage object
                    jsonReader.beginObject(){
                    while (jsonReader.hasNext()) {
                        // Assign usage values
                        if (jsonReader.nextName().equals("labels")){
                            jsonReader.beginArray();
                            while(jsonReader.hasNext()) {
                                labels.add(jsonReader.nextString());
                            }
                            jsonReader.endArray();
                        }
                        int length = 0;
                        if (jsonReader.nextName().equals("data")) {
                            jsonReader.beginArray();
                            int idx = 0;
                            while(jsonReader.hasNext()) {
                                jsonReader.beginArray();
                                Float curr_reading = daily_energy_readings.getOrDefault(labels.get(idx), 0.0f);
                                daily_energy_readings.put(labels.get(idx), curr_reading);
                                idx++;
                                jsonReader.endArray();
                            }
                            length++;
                            jsonReader.endArray();
                        }
                    }
                    jsonReader.endObject();
                }
                jsonReader.endObject();
            }
        }

        connection.disconnect()
        responseReader.close()
        jsonReader.close()



        return "";
    }
}
