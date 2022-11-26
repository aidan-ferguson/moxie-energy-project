package com.sh22.energy_saver_app;

import android.graphics.LightingColorFilter;
import android.os.Bundle;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sh22.energy_saver_app.backendhandler.ApplianceData;
import com.sh22.energy_saver_app.backendhandler.BackendInterface;

import org.json.JSONException;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "score";
    private Float score = 0.0f;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data();
                // When we get the data, update the UI


                // Some time may have passed so we need to check if the activity is now null
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Currently the score will be the daily aggregate as a percentage of some number
                        Double aggregate_daily = appliance_data.today.get(0);
                        Double limit = 500.0;

                        Integer progress = Math.toIntExact(Math.round((aggregate_daily / limit) * 100));
                        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                        TextView textView = view.findViewById(R.id.text_view_progress);
                        progressBar.setProgress(progress);
                        textView.setText(progress.toString());
                        int resultColor = ColorUtils.blendARGB(0xFF0000, 0x00FF00, score);
                        textView.getBackground().setColorFilter(new LightingColorFilter(resultColor, resultColor));
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        // Return the inflated view
        return view;
    }
}