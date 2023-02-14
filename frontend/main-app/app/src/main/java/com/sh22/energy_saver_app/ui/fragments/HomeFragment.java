package com.sh22.energy_saver_app.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.common.UserInfo;

import org.json.JSONException;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {


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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data();
                if(appliance_data == null) {
                    Log.e("SH22", "Error loading appliance data");
                    throw new IOException();
                }

                // When we get the data, update the UI
                // Some time may have passed so we need to check if the activity is now null
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Currently the score will be the daily aggregate as a percentage of some number
                        float score = SH22Utils.getEnergyScore(appliance_data, "aggregate");

                        int progress = Math.round(score * 100);
                        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                        TextView textView = view.findViewById(R.id.text_view_progress);
                        progressBar.setProgress(progress, true);
                        textView.setText(Integer.toString(progress));
                        int good_colour = ContextCompat.getColor(activity, R.color.bad_usage);
                        int bad_colour = ContextCompat.getColor(activity, R.color.good_usage);
                        int resultColor = ColorUtils.blendARGB(good_colour, bad_colour, score);

                        // Clamp upper bound of the colour
                        if(score > 1.0) {
                            resultColor = good_colour;
                        }

                        progressBar.setProgressTintList(ColorStateList.valueOf(resultColor));
                        TextView textView2 = view.findViewById(R.id.text_view);
                        textView2.setText("Use a power strip: Connecting multiple electronics to a power strip and turning off the strip when not in use can reduce standby power usage.");

                        TextView textView3 = view.findViewById(R.id.text_view2);
                        //hacky solution to make the last fews lines of text not cut out in the scroll view
                        String message ="You seem to have used less energy on lights compared to last month. This is great and shows that you are taking steps to conserve electricity. However, we've noticed an increase in the use of your electric heater. This could be due to the colder weather or a change in your habits. It's important to keep an eye on your energy usage and find ways to reduce your consumption and save on energy costs. Keep up the good work!";
                        String empty = ".                                                                                                    .";
                    textView3.setText(message+empty+empty+empty);
                    });
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();

        // Get user info to display on homepage
        new Thread(() -> {
            try {
                UserInfo userInfo = BackendInterface.GetUserInfo(view.getContext());
                if(userInfo != null) {
                    ((TextView) view.findViewById(R.id.home_fragment_heading)).setText("Welcome " + userInfo.firstname);
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }).start();

        // Return the inflated view
        return view;
    }
}