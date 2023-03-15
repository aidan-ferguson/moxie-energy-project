package com.sh22.energy_saver_app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sh22.energy_saver_app.backend.AuthenticationStatus;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.ui.activites.LoginActivity;
import com.sh22.energy_saver_app.ui.activites.MainActivity;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        ((EditText) view.findViewById(R.id.register_username)).setText(username);
        ((EditText) view.findViewById(R.id.register_password)).setText(password);

        // Callback function for when register is clicked
        view.findViewById(R.id.register_button).setOnClickListener((View v) -> {

            EditText username = (EditText) view.findViewById(R.id.register_username);
            EditText password = (EditText) view.findViewById(R.id.register_password);
            EditText firstname = (EditText) view.findViewById(R.id.register_firstname);
            EditText surname = (EditText) view.findViewById(R.id.register_surname);
            EditText password_confirm = (EditText) view.findViewById(R.id.register_password_confirm);

            // Attempt to register user
            if (!password.getText().toString().equals(password_confirm.getText().toString())) {
                password_confirm.setError("The passwords must match");
            } else {
                // Network stuff we need a separate thread
                new Thread(() -> {
                    AuthenticationStatus status = AuthenticationHandler.registerUser(getContext(),

                            // REFACTORED ???

                            EditTextToString(username),
                            EditTextToString(password),
                            EditTextToString(firstname),
                            EditTextToString(surname));

                    // Jump back on UI thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (status.success) {
                            // Registration and login success
                            Activity activity = getActivity();
                            if (activity != null) {
                                Intent intent = new Intent(activity, MainActivity.class);
                                startActivity(intent);
                                activity.finish(); // Disallow user going back after logout has ended
                            } else {
                                Log.e("SH22", "NullPtrException activity in register");
                            }
                        } else {
                            ((TextView) view.findViewById(R.id.register_error_box)).setVisibility(View.GONE);

                            try {
                                JSONObject json_response = new JSONObject(status.data);
                                if (json_response.has("non_field_errors")) {
                                    ((TextView) view.findViewById(R.id.register_error_box)).setVisibility(View.VISIBLE);
                                    JSONArray response_array = json_response.getJSONArray("non_field_errors");
                                    if (response_array.length() > 0) {
                                        ((TextView) view.findViewById(R.id.register_error_box)).setText(response_array.getString(0));
                                    }
                                }

                                // Parse individual field errors and show them

                                ParseFieldErrors(username, "username", json_response);
                                ParseFieldErrors(password, "password", json_response);
                                ParseFieldErrors(firstname, "firstname", json_response);
                                ParseFieldErrors(surname, "surname", json_response);

                            } catch (JSONException e) {
                                ((TextView) view.findViewById(R.id.register_error_box)).setVisibility(View.VISIBLE);
                                ((TextView) view.findViewById(R.id.register_error_box)).setText(Constants.INTERNAL_ERROR);
                                e.printStackTrace();
                            }
                        }
                    });
                }).start();
            }
        });

        view.findViewById(R.id.register_login_button).setOnClickListener((View v) -> {
            // Switch fragment
            LoginActivity activity = (LoginActivity) getActivity();
            if (activity != null) {
                ((LoginActivity) getActivity()).replaceFragment(LoginFragment.newInstance());
            }
        });

        return view;
    }

    private String EditTextToString(EditText label){
        return label.getText().toString();
    }

    private void ParseFieldErrors(EditText label, String labelString, JSONObject json_response) throws JSONException {
        if(json_response.has(labelString)) {
            JSONArray response_array = json_response.getJSONArray(labelString);
            if (response_array.length() > 0) {
                label.setError(response_array.getString(0));
            }
        }
    }
}