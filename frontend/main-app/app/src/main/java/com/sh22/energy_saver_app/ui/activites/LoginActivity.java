package com.sh22.energy_saver_app.ui.activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;
import com.sh22.energy_saver_app.ui.fragments.LoginFragment;
import com.sh22.energy_saver_app.ui.fragments.LoginLoadingFragment;


public class LoginActivity extends AppCompatActivity {
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        actionBar = getSupportActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        // Start with loading fragment
        replaceFragment(LoginLoadingFragment.newInstance());

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
                    replaceFragment(LoginFragment.newInstance());
                }
            });
        }).start();
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.login_frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public ActionBar getterActionBar() {
        return actionBar;
    }
}







