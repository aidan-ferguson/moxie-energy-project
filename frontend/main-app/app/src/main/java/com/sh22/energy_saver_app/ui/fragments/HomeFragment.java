package com.sh22.energy_saver_app.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.Gravity;
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
                ApplianceData appliance_data = BackendInterface.get_appliance_data(view.getContext());
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

                    });
                }
            } catch (AuthenticationException | IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Start new thread to get tips and usage report, these may take a while given OpenAI's inference time
        new Thread(() -> {
            try {
                String totd = BackendInterface.GetTOTD(view.getContext());
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Tip of the day
                        TextView textView = view.findViewById(R.id.text_view);
                        textView.setText(totd + "\n\n\n");
                    });
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String energy_report = BackendInterface.GetEnergyReport(view.getContext());
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Usage report
                        TextView textView2 = view.findViewById(R.id.text_view2);
                        textView2.setText(energy_report + "\n\n\n");
                        textView2.setGravity(Gravity.START);
                    });
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }).start();

        // Get user info to display on homepage
        new Thread(() -> {
            try {
                UserInfo userInfo = BackendInterface.GetUserInfo(view.getContext());
                if(userInfo != null) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            ((TextView) view.findViewById(R.id.home_fragment_heading)).setText("Welcome, " + userInfo.firstname);
                        });
                    }
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }).start();

        // Return the inflated view
        return view;
    }
}