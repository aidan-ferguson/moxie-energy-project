package com.sh22.energy_saver_app;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.sh22.energy_saver_app.backendhandler.ApplianceData;
import com.sh22.energy_saver_app.backendhandler.BackendInterface;

import org.json.JSONException;
import org.w3c.dom.Text;

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
                // When we get the data, update the UI


                // Some time may have passed so we need to check if the activity is now null
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Currently the score will be the daily aggregate as a percentage of some number
                        Double aggregate_daily = appliance_data.today.get(0);
                        Log.d("moxie", String.valueOf(aggregate_daily));
                        Double limit = appliance_data.weekly_average.get(0);
                        float score = (float)(aggregate_daily / limit);

                        Integer progress = Math.round(score * 100)-50;
                        TextView textView = view.findViewById(R.id.text_view_progress);
                        textView.setText(progress.toString());
                        int good_colour = ContextCompat.getColor(activity, R.color.good_usage);
                        int bad_colour = ContextCompat.getColor(activity, R.color.bad_usage);
                        int resultColor = ColorUtils.blendARGB(good_colour, bad_colour, score);

                        // Clamp upper bound of the colour
                        if(score > 1.0) {
                            resultColor = bad_colour;
                        }

                        HalfGauge gauge = view.findViewById(R.id.halfGauge);
                        gauge.setMinValue(-50);
                        gauge.setMaxValue((50));
                        gauge.setValue(progress);
                        gauge.setBackgroundColor(resultColor);

                        TextView letterGrade = view.findViewById(R.id.home_fragment_heading);
                        if(progress <= 50)
                        {
                            letterGrade.setText("F-");
                        }
                        else if (progress <= 40)
                        {
                            letterGrade.setText("F+");
                        }
                        else if(progress <= -30)
                        {
                            letterGrade.setText("D-");
                        }
                        else if(progress <= -20)
                        {
                            letterGrade.setText("D+");
                        }
                        else if (progress <= -10)
                        {
                            letterGrade.setText("C-");
                        }
                        else if (progress <= 10)
                        {
                            letterGrade.setText("C+");
                        }
                        else if (progress <= 20)
                        {
                            letterGrade.setText("B-");
                        }
                        else if (progress <= 30)
                        {
                            letterGrade.setText("B+");
                        }
                        else if (progress > 40)
                        {
                            letterGrade.setText("A-");
                        }
                        else if (progress > 50)
                        {
                            letterGrade.setText("A+");
                        }
                        letterGrade.setTextColor(resultColor);
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