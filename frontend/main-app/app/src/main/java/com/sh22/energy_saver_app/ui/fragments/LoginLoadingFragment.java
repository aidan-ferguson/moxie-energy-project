package com.sh22.energy_saver_app.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh22.energy_saver_app.R;

public class LoginLoadingFragment extends Fragment {

    public LoginLoadingFragment() {
        // Required empty public constructor
    }

    public static LoginLoadingFragment newInstance() {
        LoginLoadingFragment fragment = new LoginLoadingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_loading, container, false);
        return view;
    }
}