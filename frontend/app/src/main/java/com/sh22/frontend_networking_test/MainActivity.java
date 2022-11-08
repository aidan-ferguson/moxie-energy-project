package com.sh22.frontend_networking_test;

import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.sh22.frontend_networking_test.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        // Navigation bar stuff
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item ->{
            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.appliances:
                    replaceFragment(new AppliancesFragment());
                    break;
                case R.id.ecosystem:
                    replaceFragment(new EcosystemFragment());
                    break;
                case R.id.tips:
                    replaceFragment(new TipsFragment());
                    break;
                case R.id.settings:
                    replaceFragment(new SettingsFragment());
                    break;
            }

            return true;
        });

        Integer progress = 40;
        Float score = Float.valueOf(progress) / 100;
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        TextView textView = findViewById(R.id.text_view_progress);
        progressBar.setProgress(progress);
        textView.setText(progress.toString());
        int resultColor = ColorUtils.blendARGB(0xFF0000, 0x00FF00, score);
        textView.getBackground().setColorFilter(new LightingColorFilter(resultColor, resultColor));
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}