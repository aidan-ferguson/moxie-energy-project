package com.sh22.energy_saver_app.backend;

/**
 * Exception that can be thrown by backend function, contains a string reason.
 */
public class BackendException extends Exception {
    public String reason;

    public BackendException(String reason) {
        this.reason = reason;
    }
}
