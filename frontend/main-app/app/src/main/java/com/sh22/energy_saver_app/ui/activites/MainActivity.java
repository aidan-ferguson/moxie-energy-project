package com.sh22.energy_saver_app.ui.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.databinding.ActivityMainBinding;
import com.sh22.energy_saver_app.ui.fragments.AppliancesFragment;
import com.sh22.energy_saver_app.ui.fragments.EcosystemFragment;
import com.sh22.energy_saver_app.ui.fragments.HomeFragment;
import com.sh22.energy_saver_app.ui.fragments.SettingsFragment;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int currentFragment = R.id.home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(HomeFragment.newInstance());

        // Navigation bar stuff
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item ->{
            if(item.getItemId() != currentFragment) {
                currentFragment = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.home:
                        replaceFragment(HomeFragment.newInstance());
                        break;
                    case R.id.appliances:
                        replaceFragment(new AppliancesFragment());
                        break;
                    case R.id.ecosystem:
                        replaceFragment(EcosystemFragment.newInstance());
                        break;
                    case R.id.settings:
                        replaceFragment(new SettingsFragment());
                        break;
                }
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