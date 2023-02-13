package com.sh22.energy_saver_app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.BackendInterface;
// import com.unity3d.player.UnityPlayer;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

// // All comments in this file should have double 

public class EcosystemFragment extends Fragment {

    // // Unity player variables
    // private static UnityPlayer mUnityPlayer = null;
    FrameLayout frameLayoutForUnity;

    public EcosystemFragment() {
    }

    public static EcosystemFragment newInstance() {
        return new EcosystemFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // // Currently the score will be the daily aggregate as a percentage of some number
        Double aggregate_daily = 0.0;
        try {
            aggregate_daily = Objects.requireNonNull(BackendInterface.get_appliance_data()).today.get(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        Double limit = 500.0;
        float score = (float)(aggregate_daily / limit);

         // Create unity player and view
        //  if(mUnityPlayer == null) {
        //      mUnityPlayer = new UnityPlayer(getActivity());
        //  }

         // We need to remove any old parent
        //  if(mUnityPlayer.getParent() != null) {
        //      ((CardView)mUnityPlayer.getParent()).removeView(mUnityPlayer);
        //  }

        View view = inflater.inflate(R.layout.fragment_ecosystem, container, false);
        this.frameLayoutForUnity = view.findViewById(R.id.frameLayoutForUnity);
        // this.frameLayoutForUnity.addView(mUnityPlayer.getView(),
        //         FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

         // Initialise view
        // mUnityPlayer.requestFocus();
        // mUnityPlayer.windowFocusChanged(true);
        // mUnityPlayer.resume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

         // Set the eco-score of the player
        // UnityPlayer.UnitySendMessage("HealthManager", "SetHealth", String.valueOf(score));
        return view;
    }

    @Override
    public void onDestroy() {
        // mUnityPlayer.pause();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        // mUnityPlayer.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        // mUnityPlayer.resume();
        super.onResume();
    }
}
