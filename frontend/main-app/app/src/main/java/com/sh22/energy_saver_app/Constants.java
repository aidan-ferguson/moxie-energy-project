package com.sh22.energy_saver_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Constants {
    public static final String PREFERENCE_FILE_NAME = "preference_file_name";
    public static final String PREFERENCE_TOKEN_KEY = "AUTHENTICATION_TOKEN";
    public static final String SERVER_BASE_URL = "http://10.0.2.2:8000/api";
    public static final String INTERNAL_ERROR = "Internal error occured";

    // TODO: move to utils file
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
}
