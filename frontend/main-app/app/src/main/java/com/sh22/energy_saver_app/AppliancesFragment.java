package com.sh22.energy_saver_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh22.energy_saver_app.backendhandler.ApplianceData;
import com.sh22.energy_saver_app.backendhandler.BackendInterface;

import org.json.JSONException;

import java.io.IOException;


public class AppliancesFragment extends Fragment {


    public AppliancesFragment() {
        // Required empty public constructor
    }

    public static AppliancesFragment newInstance(String param1, String param2) {
        AppliancesFragment fragment = new AppliancesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appliances, container, false);

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data();
                // When we get the data, update the UI

                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("UI thread", appliance_data.labels.get(0));
                        }
                    });
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
        return view;
    }
}