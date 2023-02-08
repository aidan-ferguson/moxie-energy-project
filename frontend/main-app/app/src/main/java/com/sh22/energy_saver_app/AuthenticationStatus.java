package com.sh22.energy_saver_app;

// Utility class used as a return value by AuthenticationHandler to tell the rest of the code why an operation failed
public class AuthenticationStatus {
    public Boolean success;
    public String data;

    private AuthenticationStatus(Boolean _success, String _data) {
        success = _success;
        data = _data;
    }

    // Functions to clean up code in other places
    public static AuthenticationStatus FailedAuth(String reason) {
        return new AuthenticationStatus(false, reason);
    }
    public static AuthenticationStatus SuccessAuth(){
        return new AuthenticationStatus(true, "");
    }
}
