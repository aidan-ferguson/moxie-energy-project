package com.sh22.energy_saver_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sh22.energy_saver_app.backendhandler.AuthenticationHandler;


public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button LoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        LoginButton = findViewById(R.id.login_button);

        // Check authentication status, if no local token then show login page
        new Thread(() -> {
            Boolean local_auth = AuthenticationHandler.isLocallyAuthenticated(getApplicationContext());
            new Handler(Looper.getMainLooper()).post(() -> {
                if(local_auth) {
                    // Local token found, proceed
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // No local token, show login elements
                    findViewById(R.id.login_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.loading_layout).setVisibility(View.GONE);
                }
            });
        }).start();

        LoginButton.setOnClickListener((View v) -> {
            // Disable button while we authenticate
            findViewById(R.id.login_button).setEnabled(false);

            // Network stuff we need a separate thread
            new Thread(() -> {
                Boolean login_success = AuthenticationHandler.tryLogin(getApplicationContext(),
                        email.getText().toString(), password.getText().toString());
                // Jump back on UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!login_success) {
                        // TODO: Change to one error textbox
                        email.setError("Invalid Email or Password");
                    } else {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }).start();

            // Re-enable button
            findViewById(R.id.login_button).setEnabled(true);
        });
    }

}





