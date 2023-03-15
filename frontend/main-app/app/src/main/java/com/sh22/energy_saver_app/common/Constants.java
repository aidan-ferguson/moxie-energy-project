package com.sh22.energy_saver_app.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class for holding all the constants for the project, they are immutable
 */
public class Constants {
    public static final String PREFERENCE_FILE_NAME = "preference_file_name";
    public static final String PREFERENCE_TOKEN_KEY = "AUTHENTICATION_TOKEN";
    public static final String SERVER_BASE_URL = "http://ec2-18-168-148-213.eu-west-2.compute.amazonaws.com:8000/api"; // for production
    // public static final String SERVER_BASE_URL = "http://10.0.2.2:8000/api"; // for local testing
    public static final String INTERNAL_ERROR = "Internal error occured";
    public static final int SERVER_CONNECT_TIMEOUT = 3000; // Measured in milliseconds
    public static final long CACHE_TIMEOUT = 1000 * 60 * 60 * 6; // Amount of time cache takes to timeout in ms

    // Controls the "harshness" of the energy score
    public static final float ENERGY_SIGMOID_GRADIANT_STEEPNESS = 0.4f;
    public static final float ENERGY_NORMALISATION_SIGMOID_STRECH = 0.2f;
}
