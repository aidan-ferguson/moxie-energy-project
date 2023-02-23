package com.sh22.energy_saver_app.backend;

public class BackendException extends Exception {
    public String reason;

    public BackendException(String reason) {
        this.reason = reason;
    }
}
