package com.sh22.energy_saver_app.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;
import com.sh22.energy_saver_app.backend.AuthenticationStatus;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.ui.activites.LoginActivity;
import com.sh22.energy_saver_app.ui.activites.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class changeProviderFragment extends Fragment{

        public changeProviderFragment() {
            // Required empty public constructor
        }

        public static com.sh22.energy_saver_app.ui.fragments.LoginFragment newInstance() {
            com.sh22.energy_saver_app.ui.fragments.LoginFragment fragment = new com.sh22.energy_saver_app.ui.fragments.LoginFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_account, container, false);

            return view;
        }
    }
