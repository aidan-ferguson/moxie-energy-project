package com.sh22.energy_saver_app.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.SH22Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthenticationHandler {
    // Method for getting the locally stored authentication token, throws exception if it does not exist
    public static String getLocalToken(Context context) throws AuthenticationException {
        // Attempt to get locally stored token
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        if(!preferences.contains(Constants.PREFERENCE_TOKEN_KEY)) {
            throw new AuthenticationException();
        } else {
            String token = preferences.getString(Constants.PREFERENCE_TOKEN_KEY, "default");
            if(token.equals("default")) {
                throw new AuthenticationException();
            } else {
                return token;
            }
        }
    }

    // Method for storing the authentication token locally
    private static void setLocalToken(Context context, String token){
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PREFERENCE_TOKEN_KEY, token);
        editor.apply();
    }

    // Method to logout, will also clear BackendHandler cache
    public static void Logout(Context context) {
        clearLocalToken(context);
        BackendInterface.ClearCache();
    }

    // Method to clear any stored tokens
    public static void clearLocalToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.PREFERENCE_TOKEN_KEY);
        editor.apply();
    }

    // Method for checking if the user is authenticated with the backend
    public static Boolean isLocallyAuthenticated(Context context) {
        String token = null;
        try {
            token = getLocalToken(context);
        } catch (AuthenticationException e) {
            Log.e("moxie", "Failed to find token in preferences");
            return false;
        }
        return true;
    }

    // Method that will attempt to get and store a new token from the backend given an email & password
    public static AuthenticationStatus tryLogin(Context context, String email, String password) {
        // Attempt to connect to endpoint and authenticate
        // TODO: move to getBackendView
        String url_str = Constants.SERVER_BASE_URL + "/auth/get-token";
        URL url = null;
        try { url = new URL(url_str); }
        catch (MalformedURLException e) {e.printStackTrace(); return AuthenticationStatus.FailedAuth(Constants.INTERNAL_ERROR); }

        try {

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // TODO: change form data to key value pairs
            String form_data = "username=" + email + "&password=" + password + "";
            try( DataOutputStream writer = new DataOutputStream( connection.getOutputStream())) {
                writer.write(form_data.getBytes(StandardCharsets.UTF_8 ));
            }
            connection.connect();

            // If the response code is anything but success, get the error string and return false
            int response_code = connection.getResponseCode();
            if (response_code != 200) {
                String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
                Log.e("sh22", "BackendInterface::tryLogin server returned code " + response_code);
                Log.e("moxie", "BackendInterface::tryLogin server returned: " + error_reason);
                connection.disconnect();
                return AuthenticationStatus.FailedAuth(error_reason);
            } else {
                // Successful authentication, store and return true
                String token = new JSONObject(SH22Utils.readFullStream(connection.getInputStream())).getString("token");
                setLocalToken(context, token);
                connection.disconnect();
                return AuthenticationStatus.SuccessAuth();
            }
        } catch (IOException | JSONException e) { e.printStackTrace(); return AuthenticationStatus.FailedAuth(Constants.INTERNAL_ERROR);}
    }

    public static AuthenticationStatus registerUser(Context context, String username, String password, String firstname, String surname) {
        String url_str = Constants.SERVER_BASE_URL + "/auth/register";
        URL url = null;
        try { url = new URL(url_str); }
        catch (MalformedURLException e) {e.printStackTrace(); return AuthenticationStatus.FailedAuth(Constants.INTERNAL_ERROR); }

        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String form_data = "username=" + username + "&password=" + password + "&firstname=" + firstname + "&surname=" + surname;
            try( DataOutputStream writer = new DataOutputStream( connection.getOutputStream())) {
                writer.write(form_data.getBytes(StandardCharsets.UTF_8 ));
            }
            connection.connect();

            // If the response code is anything but success, get the error string and return false
            int response_code = connection.getResponseCode();
            if (response_code != 202) {
                Log.e("sh22", "BackendInterface::tryLogin server returned failure code " + response_code);
                String error_reason = SH22Utils.readFullStream(connection.getErrorStream());
                connection.disconnect();
                return AuthenticationStatus.FailedAuth(error_reason);
            } else {
                // Successful registration, attempt login
                return tryLogin(context, username, password);
            }
        } catch (IOException e) { e.printStackTrace(); return AuthenticationStatus.FailedAuth(Constants.INTERNAL_ERROR);}
    }
}
