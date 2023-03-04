package com.sh22.energy_saver_app.backend;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.common.CacheObject;
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
    private static CacheObject<ApplianceData> cached_appliance = new CacheObject<>();
    private static CacheObject<Map<String, Double>> cached_national_averages = new CacheObject<>();
    private static CacheObject<UserInfo> cached_user_info = new CacheObject<>();
    private static CacheObject<String> cached_totd = new CacheObject<>();
    private static CacheObject<String> cached_report = new CacheObject<>();
    private static HashMap<String, CacheObject<String>> cached_appliance_tips = new HashMap<>();


    // Clear the local cache, used when the user signs out
    public static void ClearCache() {
        cached_appliance = null;
        cached_national_averages = null;
        cached_user_info = null;
        cached_totd = null;
        cached_report = null;
    }
    // TODO: logout on all authentication exceptions with error
    // handle all backend exceptions

    // Function to get the past weeks energy usage and todays energy usage per device
    public static ApplianceData get_appliance_data(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_appliance.GetObject() != null) {
                return cached_appliance.GetObject();
            }

            try {
                HashMap<String, String> requestProperties = new HashMap<>();
                requestProperties.put("Authorization", "Token " + token);

                String data = SH22Utils.getBackendView("/usage/appliances", requestProperties);
                Log.d("SH22", data);
                JSONObject json_response = new JSONObject(data);
                if(!json_response.getBoolean("success")){
                    throw new BackendException(json_response.getString("reason"));
                }

                JSONObject energy_json = json_response.getJSONObject("data");
                JSONArray json_labels = energy_json.getJSONArray("labels");
                JSONArray json_initial_usage = energy_json.getJSONArray("initial_usage");
                JSONArray json_previous_week = energy_json.getJSONArray("previous_week");
                JSONArray json_current_day = energy_json.getJSONArray("today");
                ApplianceData applianceData = new ApplianceData();

                applianceData.energy_score = (float)energy_json.getDouble("energy_score");
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

                cached_appliance.SetObject(applianceData);
                return applianceData;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when trying to speak to the server");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when processing your data");
            }

    }

    // Method to get the national averages from the database
    public static Map<String, Double> GetNationalAverages() throws BackendException {

            if (cached_national_averages.GetObject() != null) {
                return cached_national_averages.GetObject();
            }
            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/usage/national-average", null);
                Map<String, Double> averages = new HashMap<>();
                JSONObject json_response = new JSONObject(response);
                if(!json_response.getBoolean("success")){
                    throw new BackendException(json_response.getString("reason"));
                }

                JSONObject national_data = json_response.getJSONObject("data");
                for (Iterator<String> it = national_data.keys(); it.hasNext(); ) {
                    String key = it.next();
                    averages.put(key, national_data.getDouble(key));
                }
                cached_national_averages.SetObject(averages);

                return averages;
            } catch (IOException | AuthenticationException e) {
                // Catch AuthenticationException as the national averages view is accessible to anyone
                e.printStackTrace();
                throw new BackendException("An error occurred when trying to speak to the server");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when processing your data");
            }

    }

    // Get user information stored in the backend
    public static UserInfo GetUserInfo(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);


            if (cached_user_info.GetObject() != null) {
                return cached_user_info.GetObject();
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/user/information", requestProperties);
                JSONObject json_response = new JSONObject(response);
                if(!json_response.getBoolean("success")){
                    throw new BackendException(json_response.getString("reason"));
                }
                JSONObject json_data = json_response.getJSONObject("data");
                UserInfo userInfo = new UserInfo(
                        json_data.getInt("id"),
                        json_data.getString("username"),
                        json_data.getString("firstname"),
                        json_data.getString("surname"));
                cached_user_info.SetObject(userInfo);
                return userInfo;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when trying to speak to the server");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when processing your data");
            }

    }

    public static String GetTOTD(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_totd.GetObject() != null) {
                return cached_totd.GetObject();
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/tips/totd", requestProperties);
                JSONObject json_response = new JSONObject(response);
                String totd = json_response.getString("data");
                if(!json_response.getBoolean("success")){
                    throw new BackendException(json_response.getString("reason"));
                }
                cached_totd.SetObject(totd);
                return totd;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when trying to speak to the server");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when processing your data");
            }

    }

    public static String GetEnergyReport(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

            if (cached_report.GetObject() != null) {
                return cached_report.GetObject();
            }

            HashMap<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Authorization", "Token " + token);

            try {
                // Successful authentication, store and return true
                String response = SH22Utils.getBackendView("/tips/energy-report", requestProperties);
                JSONObject json_response = new JSONObject(response);
                if(!json_response.getBoolean("success")){
                    throw new BackendException(json_response.getString("reason"));
                }
                String report = json_response.getString("data");
                cached_report.SetObject(report);
                return report;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when trying to speak to the server");
            } catch (JSONException e) {
                e.printStackTrace();
                throw new BackendException("An error occurred when processing your data");
            }
    }

    // Will return a friends object which contains actual friends and friend requests
    public static Friends GetFriends(Context context) throws AuthenticationException, BackendException {
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
                throw new BackendException(json_response.getString("reason"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when processing your data");
        }
    }

    // Attempt to connect to endpoint and authenticate
    public static boolean AcceptFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
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
                throw new BackendException(json_response.getString("reason"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when processing your data");
        }
    }

    // Create a new friend request
    public static boolean CreateFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
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
                throw new BackendException(json_response.getString("reason"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when processing your data");
        }
    }

    // Deny a new friend request
    public static boolean DenyFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        // TODO: cache
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=deny_request&user_id=" + user_id);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")){
                return true;
            } else {
                throw new BackendException(json_response.getString("reason"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when processing your data");
        }
    }

    public static String GetApplianceTip(Context context, String device_name) throws AuthenticationException, BackendException {
        // Check for a cache hit first
        if(cached_appliance_tips.containsKey(device_name)){
            CacheObject<String> cached_object = cached_appliance_tips.get(device_name);
            if(cached_object != null && cached_object.GetObject() != null) {
                return cached_object.GetObject();
            }
        }

        // No local cache, need to contact backend
        String token = AuthenticationHandler.getLocalToken(context);
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/tips/appliance?device=" + device_name, requestProperties);
            JSONObject json_response = new JSONObject(response);
            if(json_response.getBoolean("success")){
                return json_response.getString("data");
            } else {
                throw new BackendException(json_response.getString("reason"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when processing your data");
        }
    }
 }
