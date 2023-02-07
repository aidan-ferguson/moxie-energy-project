package com.sh22.energy_saver_app.backendhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sh22.energy_saver_app.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AuthenticationHandler {
    // Method for getting the locally stored authentication token, throws exception if it does not exist
    private static String getLocalToken(Context context) throws NoSuchFieldException {
        // Attempt to get locally stored token
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        if(!preferences.contains(Constants.PREFERENCE_TOKEN_KEY)) {
            throw new NoSuchFieldException();
        } else {
            String token = preferences.getString(Constants.PREFERENCE_TOKEN_KEY, "default");
            if(token.equals("default")) {
                throw new NoSuchFieldException();
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
        } catch (NoSuchFieldException e) {
            Log.e("moxie", "Failed to find token in preferences");
            return false;
        }
        return true;
    }

    // Utility function to read a full input stream into a string
    private static String readFullStream(InputStream stream) throws IOException {
        InputStreamReader inputStream = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(inputStream);
        StringBuilder buffer = new StringBuilder();
        String line;
        while((line = reader.readLine())!= null){
            buffer.append(line);
        }
        return buffer.toString();
    }

    // Method that will attempt to get and store a new token from the backend given an email & password
    public static Boolean tryLogin(Context context, String email, String password) {
        // Attempt to connect to endpoint and authenticate
        String url_str = Constants.SERVER_BASE_URL + "/auth/get-token";
        Log.d("moxie", url_str);
        URL url = null;
        try { url = new URL(url_str); }
        catch (MalformedURLException e) {e.printStackTrace(); return false; }

        try {

            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String form_data = "username=" + email + "&password=" + password + "";
            try( DataOutputStream writer = new DataOutputStream( connection.getOutputStream())) {
                writer.write(form_data.getBytes(StandardCharsets.UTF_8 ));
            }
            connection.connect();

            // If the response code is anything but success, get the error string and return false
            int response_code = connection.getResponseCode();
            if (response_code != 200) {
                // TODO: Return reason to login page
                Log.i("sh22", "BackendInterface::tryLogin server returned code " + response_code);
                Log.d("moxie", "BackendInterface::tryLogin server returned: " + readFullStream(connection.getErrorStream()));
                connection.disconnect();
                return false;
            } else {
                // Successful authentication, store and return true
                String token = new JSONObject(readFullStream(connection.getInputStream())).getString("token");
                setLocalToken(context, token);
                connection.disconnect();
                return true;
            }
        } catch (IOException | JSONException e) { e.printStackTrace(); return false;}
    }
}
