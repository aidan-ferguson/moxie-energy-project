package com.sh22.energy_saver_app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sh22.energy_saver_app.backend.AuthenticationStatus;
import com.sh22.energy_saver_app.ui.activites.LoginActivity;
import com.sh22.energy_saver_app.ui.activites.MainActivity;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;


public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText email = view.findViewById(R.id.username);
        EditText password = view.findViewById(R.id.password);
        Button LoginButton = view.findViewById(R.id.login_button);
        Button RegisterButton = view.findViewById(R.id.login_register_button);

        LoginButton.setOnClickListener((View v) -> {
            // Disable button while we authenticate
            view.findViewById(R.id.login_button).setEnabled(false);

            // Network stuff we need a separate thread
            new Thread(() -> {
                AuthenticationStatus login_status = AuthenticationHandler.tryLogin(view.getContext(),
                        email.getText().toString(), password.getText().toString());
                // Jump back on UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!login_status.success) {
                        // TODO: Change to one error textbox
                        email.setError(login_status.data);
                    } else {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }).start();

            // Re-enable button
            view.findViewById(R.id.login_button).setEnabled(true);
        });

        RegisterButton.setOnClickListener((View v) -> {
            // Switch fragment
            LoginActivity activity = (LoginActivity)getActivity();
            if(activity != null) {
                ((LoginActivity) getActivity()).replaceFragment(RegisterFragment.newInstance(email.getText().toString(), password.getText().toString()));
            }
        });

        return view;
    }
}