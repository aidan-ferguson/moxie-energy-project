package com.sh22.energy_saver_app.common;

import com.sh22.energy_saver_app.BuildConfig;

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
    public static final String SERVER_BASE_URL = BuildConfig.SERVER_BASE_URL; // This is taken from gradle at build time (allows us to use localhost for testing)
    public static final String INTERNAL_ERROR = "Internal error occured";
    public static final int SERVER_CONNECT_TIMEOUT = 3000; // Measured in milliseconds
    public static final long CACHE_TIMEOUT = 1000 * 60 * 60 * 6; // Amount of time cache takes to timeout in ms

    // Controls the "harshness" of the energy score
    public static final float ENERGY_SIGMOID_GRADIANT_STEEPNESS = 0.4f;
    public static final float ENERGY_NORMALISATION_SIGMOID_STRECH = 0.2f;
}
