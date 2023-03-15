package com.sh22.energy_saver_app.backend;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.common.CacheObject;
import com.sh22.energy_saver_app.common.FriendRelationship;
import com.sh22.energy_saver_app.common.FriendRequest;
import com.sh22.energy_saver_app.common.Friends;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.common.UserInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is the
 */
public class BackendInterface {
    private static final CacheObject<ApplianceData> cached_appliance = new CacheObject<>();
    private static final CacheObject<Map<String, Double>> cached_national_averages = new CacheObject<>();
    private static final CacheObject<UserInfo> cached_user_info = new CacheObject<>();
    private static final CacheObject<String> cached_totd = new CacheObject<>();
    private static final CacheObject<String> cached_report = new CacheObject<>();
    private static final HashMap<String, CacheObject<String>> cached_appliance_tips = new HashMap<>();

    /**
     * Clear the local cache, used when the user signs out
     */
    public static void ClearCache() {
        cached_appliance.SetObject(null);
        cached_national_averages.SetObject(null);
        cached_user_info.SetObject(null);
        cached_totd.SetObject(null);
        cached_report.SetObject(null);
        for (String device : cached_appliance_tips.keySet()) {
            Objects.requireNonNull(cached_appliance_tips.get(device)).SetObject(null);
        }
    }

    /**
     * Function to get the past weeks energy usage and today's energy usage per device
     *
     * @param context - The current application context
     * @return An ApplianceData object containing all the data form the backend
     * @throws AuthenticationException - On user authentication failure
     * @throws BackendException        - On error in backend
     */
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
            if (!json_response.getBoolean("success")) {
                throw new BackendException(json_response.getString("reason"));
            }

            JSONObject energy_json = json_response.getJSONObject("data");
            JSONArray json_labels = energy_json.getJSONArray("labels");
            JSONArray json_initial_usage = energy_json.getJSONArray("initial_usage");
            JSONArray json_previous_week = energy_json.getJSONArray("previous_week");
            JSONArray json_current_day = energy_json.getJSONArray("today");
            ApplianceData applianceData = new ApplianceData();

            applianceData.energy_score = (float) energy_json.getDouble("energy_score");
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

    /**
     * Get the national averages device usage from the backend
     *
     * @return - Method to get the national averages from the database
     * @throws BackendException - On failure in backend
     */
    public static Map<String, Double> GetNationalAverages() throws BackendException {

        if (cached_national_averages.GetObject() != null) {
            return cached_national_averages.GetObject();
        }
        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/usage/national-average", null);
            Map<String, Double> averages = new HashMap<>();
            JSONObject json_response = new JSONObject(response);
            if (!json_response.getBoolean("success")) {
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

    /**
     * Get user information stored in the backend
     *
     * @param context - The current application context
     * @return Returns the user info of the currently signed in user
     * @throws AuthenticationException - On authentication error in the backend
     * @throws BackendException        - On failure in the backend
     */
    public static UserInfo GetUserInfo(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        // Check if cache contains any user info
        if (cached_user_info.GetObject() != null) {
            return cached_user_info.GetObject();
        }

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/user/information", requestProperties);
            JSONObject json_response = new JSONObject(response);
            if (!json_response.getBoolean("success")) {
                throw new BackendException(json_response.getString("reason"));
            }
            JSONObject json_data = json_response.getJSONObject("data");
            UserInfo userInfo = new UserInfo(
                    json_data.getInt("id"),
                    json_data.getString("username"),
                    json_data.getString("firstname"),
                    json_data.getString("surname"),
                    json_data.getString("data_provider"),
                    json_data.getDouble("energy_score"));
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

    /**
     * Function to the the current tip of the day
     * @param context - The current application context
     * @return - A string containing the tip
     * @throws AuthenticationException - On authentication error with the backend
     * @throws BackendException - On error in the backend
     */
    public static String GetTOTD(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        // Check if a cache object exists
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
            if (!json_response.getBoolean("success")) {
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

    /**
     * Function to get the AI generated personal energy report from the backend
     * @param context - The current application context
     * @return - String containing the tip text
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static String GetEnergyReport(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        // Attempt to get a cache object
        if (cached_report.GetObject() != null) {
            return cached_report.GetObject();
        }

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/tips/energy-report", requestProperties);
            JSONObject json_response = new JSONObject(response);
            if (!json_response.getBoolean("success")) {
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

    /**
     * Get the friends and friend requests from the backend
     * @param context - The current application context
     * @return - Friends object containing FriendRelationship array and FriendRequest array
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static Friends GetFriends(Context context) throws AuthenticationException, BackendException {
        // Note: there is no cache in this function as friends need to be up to date
        String token = AuthenticationHandler.getLocalToken(context);

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/user/friends", requestProperties);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if (json_response.getBoolean("success")) {
                json_response = json_response.getJSONObject("data");

                // Add current friends
                ArrayList<FriendRelationship> friends = new ArrayList<>();
                JSONArray friends_array = json_response.getJSONArray("friends");
                for (int idx = 0; idx < friends_array.length(); idx++) {
                    JSONObject friend_json = (JSONObject) friends_array.get(idx);
                    FriendRelationship friend = new FriendRelationship(friend_json.getInt("id"), friend_json.getString("firstname"), friend_json.getString("surname"), friend_json.getDouble("score"));
                    friends.add(friend);
                }

                // Add friend requests
                ArrayList<FriendRequest> requests = new ArrayList<>();
                JSONArray requests_json_array = json_response.getJSONArray("requests");
                for (int idx = 0; idx < requests_json_array.length(); idx++) {
                    JSONObject request_json = (JSONObject) requests_json_array.get(idx);
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

    /**
     * Will accept any friend requests from a given ID
     * @param context - The application context
     * @param user_id - The user id of the user to accept the request from
     * @return True on success, False on failure
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static boolean AcceptFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=accept_request&user_id=" + user_id);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if (json_response.getBoolean("success")) {
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

    /**
     * Create a new friend request from the current user
     * @param context - The current application context
     * @param user_id - The user id to send the friend request to
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static void CreateFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=make_request&user_id=" + user_id);
            Log.d("SH22", response);
            JSONObject json_response = new JSONObject(response);
            if(!json_response.getBoolean("success")) {
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

    /**
     * Deny a friend request from a specific user
     * @param context - The context of the application
     * @param user_id - The user id of the user whose freind request to deny
     * @return True on success, False on failure
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    // Deny a new friend request
    public static boolean DenyFriendRequest(Context context, int user_id) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.postBackendView("/user/friends", requestProperties, "action=deny_request&user_id=" + user_id);
            JSONObject json_response = new JSONObject(response);
            if (json_response.getBoolean("success")) {
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

    /**
     * Get a AI generated tip for a specific appliance
     * @param context - The current application context
     * @param device_name - A string containing the name of the device
     * @return The string containing the generated tip
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static String GetApplianceTip(Context context, String device_name) throws AuthenticationException, BackendException {
        // Check for a cache hit first
        if (cached_appliance_tips.containsKey(device_name)) {
            CacheObject<String> cached_object = cached_appliance_tips.get(device_name);
            if (cached_object != null && cached_object.GetObject() != null) {
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
            if (json_response.getBoolean("success")) {
                String tip = json_response.getString("data");
                CacheObject<String> tip_cache = new CacheObject<>();
                tip_cache.SetObject(tip);
                cached_appliance_tips.put(device_name, tip_cache);
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

    /**
     * Delete the current users account
     * @param context - The current application context
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static void DeleteAccount(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);
        try {
            SH22Utils.postBackendView("/user/delete-account", requestProperties, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        }
    }

    /**
     * Get a list of all the available data providers
     * @param context - The current application context
     * @return An array of strings of data provider options
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static ArrayList<String> GetDataProviders(Context context) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            String response = SH22Utils.getBackendView("/usage/available-data-providers", requestProperties);
            JSONObject json_response = new JSONObject(response);
            if (json_response.getBoolean("success")) {
                JSONArray json_array = json_response.getJSONArray("data");
                ArrayList<String> data_providers = new ArrayList<>();
                for (int i = 0; i < json_array.length(); i++) {
                    data_providers.add(json_array.getString(i));
                }
                return data_providers;
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

    /**
     * Update the user info in the backend
     * @param context - Current application context
     * @param new_info - A HashMap of Key-Value pairs containing the new user data
     * @throws AuthenticationException - On authentication failure in backend
     * @throws BackendException - On failure in the backend
     */
    public static void UpdateUserInfo(Context context, HashMap<String, String> new_info) throws AuthenticationException, BackendException {
        String token = AuthenticationHandler.getLocalToken(context);

        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Authorization", "Token " + token);

        try {
            // Successful authentication, store and return true
            StringBuilder form_data = new StringBuilder("action=update");
            for (String key : new_info.keySet()) {
                form_data.append("&").append(key).append("=").append(new_info.get(key));
            }
            SH22Utils.postBackendView("/user/information", requestProperties, form_data.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new BackendException("An error occurred when trying to speak to the server");
        }
    }
}
