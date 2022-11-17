package com.sh22.energy_saver_app;

import android.graphics.LightingColorFilter;
import android.os.Bundle;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    public static HomeFragment newInstance(String score) {
        Bundle bundle = new Bundle();
        bundle.putString("score", score.toString());
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = Float.valueOf(getArguments().getString(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Integer progress = Math.round(score*100);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);
        TextView textView = view.findViewById(R.id.text_view_progress);
        progressBar.setProgress(progress);
        textView.setText(progress.toString());
        int resultColor = ColorUtils.blendARGB(0xFF0000, 0x00FF00, score);
        textView.getBackground().setColorFilter(new LightingColorFilter(resultColor, resultColor));

        // Return the inflated view
        return view;
    }
}