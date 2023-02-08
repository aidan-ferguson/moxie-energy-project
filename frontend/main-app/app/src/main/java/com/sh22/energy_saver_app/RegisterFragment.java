package com.sh22.energy_saver_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sh22.energy_saver_app.backendhandler.AuthenticationHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";

    private String username;
    private String password;

    public RegisterFragment() {
        // Required empty public constructor
    }


    public static RegisterFragment newInstance(String username, String password) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            password = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Set values that may have been carried over from login fragment
        ((EditText)view.findViewById(R.id.register_username)).setText(username);
        ((EditText)view.findViewById(R.id.register_password)).setText(password);

        // Callback function for when register is clicked
        view.findViewById(R.id.register_button).setOnClickListener((View v) -> {
            String username = ((EditText)view.findViewById(R.id.register_username)).getText().toString();
            String password = ((EditText)view.findViewById(R.id.register_password)).getText().toString();
            String firstname = ((EditText)view.findViewById(R.id.register_firstname)).getText().toString();
            String surname = ((EditText)view.findViewById(R.id.register_surname)).getText().toString();
            String password_confirm = ((EditText)view.findViewById(R.id.register_password_confirm)).getText().toString();

            // Attempt to register user
            if(!password.equals(password_confirm)) {
                ((EditText)view.findViewById(R.id.register_password_confirm)).setError("The passwords must match");
            } else {
                // Network stuff we need a separate thread
                new Thread(() -> {
                    AuthenticationStatus status = AuthenticationHandler.registerUser(getContext(), username, password, firstname, surname);
                    // Jump back on UI thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (status.success) {
                            // Registration and login success
                            LoginActivity activity = (LoginActivity)getActivity();
                            if(activity != null) {
                                ((LoginActivity) getActivity()).replaceFragment(RegisterInformationFragment.newInstance());
                            }
                        } else {
                            ((EditText)view.findViewById(R.id.register_password_confirm)).setError(status.data);
                        }
                    });
                }).start();
            }
        });

        view.findViewById(R.id.register_login_button).setOnClickListener((View v) -> {
            // Switch fragment
            LoginActivity activity = (LoginActivity)getActivity();
            if(activity != null) {
                ((LoginActivity) getActivity()).replaceFragment(LoginFragment.newInstance());
            }
        });

        return view;
    }
}