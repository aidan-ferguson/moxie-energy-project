package com.sh22.energy_saver_app.ui.activites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationHandler;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.databinding.ActivityMainBinding;
import com.sh22.energy_saver_app.ui.fragments.AppliancesFragment;
import com.sh22.energy_saver_app.ui.fragments.EcosystemFragment;
import com.sh22.energy_saver_app.ui.fragments.HomeFragment;
import com.sh22.energy_saver_app.ui.fragments.SettingsFragment;
import com.sh22.energy_saver_app.ui.fragments.changeProviderFragment;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    int currentFragment = R.id.home;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#04244C"));
        // Set BackgroundDrawable
        actionBar.setBackgroundDrawable(colorDrawable);
        getterActionBar().setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Welcome</font>"));

        actionBar.setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_settings_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                SH22Utils.Logout(this);
                break;
            case R.id.changeDataProvider:
                replaceFragment(new changeProviderFragment());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @NonNull
    public ActionBar getterActionBar() {
        return actionBar;
    }
}