package com.sh22.energy_saver_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sh22.energy_saver_app.backendhandler.ApplianceData;
import com.sh22.energy_saver_app.backendhandler.BackendInterface;
import com.sh22.energy_saver_app.databinding.ActivityMainBinding;

import org.json.JSONException;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(HomeFragment.newInstance());

        // Navigation bar stuff
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item ->{
            switch(item.getItemId()){
                case R.id.home:
                    replaceFragment(HomeFragment.newInstance());
                    break;
                case R.id.appliances:
                    replaceFragment(new AppliancesFragment());
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
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}