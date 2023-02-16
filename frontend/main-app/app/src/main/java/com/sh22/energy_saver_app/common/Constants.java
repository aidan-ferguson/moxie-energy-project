package com.sh22.energy_saver_app.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Constants {
    public static final String PREFERENCE_FILE_NAME = "preference_file_name";
    public static final String PREFERENCE_TOKEN_KEY = "AUTHENTICATION_TOKEN";
    public static final String SERVER_BASE_URL = "http://10.0.2.2:8000/api";
    public static final String INTERNAL_ERROR = "Internal error occured";

    // Controls the "harshness" of the energy score
    public static final float ENERGY_SIGMOID_GRADIANT_STEEPNESS = 0.4f;
    public static final float ENERGY_NORMALISATION_SIGMOID_STRECH = 0.2f;
}