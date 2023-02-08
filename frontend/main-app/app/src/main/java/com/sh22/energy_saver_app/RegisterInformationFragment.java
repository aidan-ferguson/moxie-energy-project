package com.sh22.energy_saver_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegisterInformationFragment extends Fragment {


    public RegisterInformationFragment() {
        // Required empty public constructor
    }

    public static RegisterInformationFragment newInstance() {
        RegisterInformationFragment fragment = new RegisterInformationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_information, container, false);

        view.findViewById(R.id.register_information_submit).setOnClickListener((View v) -> {
            
        });

        return view;
    }
}