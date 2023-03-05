package com.sh22.energy_saver_app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#62A526"));
        ((LoginActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((LoginActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#FFFFFF'>Log in</font></div></center>"));
        ((LoginActivity)getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((LoginActivity)getActivity()).getterActionBar().setCustomView(R.layout.action_bar_appliances);
        LoginButton.setOnClickListener((View v) -> {
            // Disable button while we authenticate
            view.findViewById(R.id.login_button).setEnabled(false);

            // Network stuff we need a separate thread
            new Thread(() -> {
                AuthenticationStatus login_status = AuthenticationHandler.tryLogin(view.getContext(),
                        email.getText().toString(), password.getText().toString());
                // Jump back on UI thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    ((TextView) view.findViewById(R.id.login_error_box)).setText("");
                    ((TextView) view.findViewById(R.id.login_error_box)).setVisibility(View.GONE);
                    if (!login_status.success) {
                        // Parse the error
                        try {
                            JSONObject json_response = new JSONObject(login_status.data);
                            if (json_response.has("non_field_errors")) {
                                ((TextView) view.findViewById(R.id.login_error_box)).setVisibility(View.VISIBLE);
                                JSONArray response_array = json_response.getJSONArray("non_field_errors");
                                if (response_array.length() > 0) {
                                    ((TextView) view.findViewById(R.id.login_error_box)).setText(response_array.getString(0));
                                }
                            }

                            // Parse individual field errors and show them
                            if (json_response.has("username")) {
                                JSONArray response_array = json_response.getJSONArray("username");
                                if (response_array.length() > 0) {
                                    email.setError(response_array.getString(0));
                                }
                            }
                            if (json_response.has("password")) {
                                JSONArray response_array = json_response.getJSONArray("password");
                                if (response_array.length() > 0) {
                                    password.setError(response_array.getString(0));
                                }
                            }

                        } catch (JSONException e) {
                            ((TextView) view.findViewById(R.id.login_error_box)).setVisibility(View.VISIBLE);
                            ((TextView) view.findViewById(R.id.login_error_box)).setText(Constants.INTERNAL_ERROR);
                            e.printStackTrace();
                        }
                    } else {
                        Activity activity = getActivity();
                        if (activity != null) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            activity.finish(); // Disallow user going back after logout has ended
                        } else {
                            Log.e("SH22", "NullPtrException activity in login");
                        }
                    }
                });
            }).start();

            // Re-enable button
            view.findViewById(R.id.login_button).setEnabled(true);
        });

        RegisterButton.setOnClickListener((View v) -> {
            // Switch fragment
            LoginActivity activity = (LoginActivity) getActivity();
            if (activity != null) {
                ((LoginActivity) getActivity()).replaceFragment(RegisterFragment.newInstance(email.getText().toString(), password.getText().toString()));
            }
        });

        return view;
    }

}
