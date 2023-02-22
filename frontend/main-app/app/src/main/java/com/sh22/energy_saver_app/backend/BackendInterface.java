package com.sh22.energy_saver_app.backend;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.FriendRelationship;
import com.sh22.energy_saver_app.common.FriendRequest;
import com.sh22.energy_saver_app.common.Friends;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.common.UserInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BackendInterface {
    private static ApplianceData cached_appliance = null;
    private static Map<String, Double> cached_national_averages = null;
    private static UserInfo cached_user_info = null;
    private static String cached_totd = null;
    private static String cached_report = null;

    // mutex like object used to stop multiple calls to the backend at once
    private static final Object appliance_lock = new Object();
    private static final Object national_average_lock = new Object();
    private static final Object user_info_lock = new Object();
    private static final Object totd_lock = new Object();
    private static final Object report_lock = new Object();


    // Clear the local cache, used when the user signs out
    public static void ClearCache() {
        cached_appliance = null;
        cached_national_averages = null;
        cached_user_info = null;
        cached_totd = null;
        cached_report = null;
    }

    // Function to get the past weeks energy usage and todays energy usage per device
    public static ApplianceData get_appliance_data(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_appliance != null) {
                return cached_appliance;
            }

            try {
                HashMap<String, String> requestProperties = new HashMap<>();
                requestProperties.put("Authorization", "Token " + token);

                String data = SH22Utils.getBackendView("/usage/appliances", requestProperties);
                Log.d("SH22", data);

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

                cached_appliance = applianceData;
                return applianceData;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }

    }

    // Method to get the national averages from the database
    public static Map<String, Double> GetNationalAverages() {

            if (cached_national_averages != null) {
                return cached_national_averages;
            }
            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/usage/national-average", null);
                Map<String, Double> averages = new HashMap<>();
                JSONObject json_data = new JSONObject(response);
                for (Iterator<String> it = json_data.keys(); it.hasNext(); ) {
                    String key = it.next();
                    averages.put(key, json_data.getDouble(key));
                }
                cached_national_averages = averages;

                return averages;
            } catch (IOException | JSONException | AuthenticationException e) {
                e.printStackTrace();
                return null;
            }

    }

    // Get user information stored in the backend
    public static UserInfo GetUserInfo(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);


            if (cached_user_info != null) {
                return cached_user_info;
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/user/information", requestProperties);
                JSONObject json_data = new JSONObject(response).getJSONObject("user_data");
                UserInfo userInfo = new UserInfo(
                        json_data.getInt("id"),
                        json_data.getString("username"),
                        json_data.getString("firstname"),
                        json_data.getString("surname"));
                cached_user_info = userInfo;
                return userInfo;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }

    }

    public static String GetTOTD(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_totd != null) {
                return cached_totd;
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/tips/totd", requestProperties);
                String totd = new JSONObject(response).getString("data");
                cached_totd = totd;
                return totd;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }

    }

    public static String GetEnergyReport(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_report != null) {
                return cached_report;
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/tips/energy-report", requestProperties);
                String report = new JSONObject(response).getString("data");
                cached_report = report;
                return report;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
    }

    // Will return a friends object which contains actual friends and friend requests
    public static Friends GetFriends(Context context) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

        // TODO: cache
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/user/friends", requestProperties);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")) {
                json_response = json_response.getJSONObject("data");

                // Add current friends
                ArrayList<FriendRelationship> friends = new ArrayList<>();
                JSONArray friends_array =  json_response.getJSONArray("friends");
                for(int idx = 0; idx < friends_array.length(); idx++) {
                    JSONObject friend_json = (JSONObject)friends_array.get(idx);
                    FriendRelationship friend = new FriendRelationship(friend_json.getInt("id"), friend_json.getString("firstname"), friend_json.getString("surname"));
                    friends.add(friend);
                }

                // Add friend requests
                ArrayList<FriendRequest> requests = new ArrayList<>();
                JSONArray requests_json_array =  json_response.getJSONArray("requests");
                for(int idx = 0; idx < requests_json_array.length(); idx++) {
                    JSONObject request_json = (JSONObject)requests_json_array.get(idx);
                    FriendRequest request = new FriendRequest(request_json.getInt("id"), request_json.getString("firstname"), request_json.getString("surname"));
                    requests.add(request);
                }


                return new Friends(friends, requests);
            } else {
                throw new IOException(json_response.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Attempt to connect to endpoint and authenticate
    public static boolean AcceptFriendRequest(Context context, int user_id) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

        // TODO: cache
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=accept_request&user_id=" + user_id);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")){
                return true;
            } else {
                throw new IOException(json_response.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create a new friend request
    public static boolean CreateFriendRequest(Context context, int user_id) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

        // TODO: cache
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=make_request&user_id=" + user_id);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")){
                return true;
            } else {
                throw new IOException(json_response.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deny a new friend request
    public static boolean DenyFriendRequest(Context context, int user_id) throws AuthenticationException {
        String token = AuthenticationHandler.getLocalToken(context);

        // TODO: cache
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=deny_request&user_id=" + user_id);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")){
                return true;
            } else {
                throw new IOException(json_response.getString("reason"));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
 }
