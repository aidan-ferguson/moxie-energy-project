package com.sh22.energy_saver_app.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.ui.activites.MainActivity;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

// Template file for when building with unity version

public class EcosystemFragment extends Fragment {

    // // Unity player variables
    private static UnityPlayer mUnityPlayer = null;
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
        View view = inflater.inflate(R.layout.fragment_ecosystem, container, false);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#62A526"));
        ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<div><font color='#FFFFFF'>Your Ecosystem</font></div>"));
        ((MainActivity)getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MainActivity)getActivity()).getterActionBar().setCustomView(R.layout.action_bar_ecosystem);

         // Create unity player and view
         if(mUnityPlayer == null) {
             mUnityPlayer = new UnityPlayer(getActivity());
         }

         // We need to remove any old parent
         if(mUnityPlayer.getParent() != null) {
             ((CardView)mUnityPlayer.getParent()).removeView(mUnityPlayer);
         }

        this.frameLayoutForUnity = view.findViewById(R.id.frameLayoutForUnity);
        this.frameLayoutForUnity.addView(mUnityPlayer.getView(),
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

         // Initialise view
        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);
        mUnityPlayer.resume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the eco-score of the player
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data(view.getContext());
                if(appliance_data != null) {
                    float score = appliance_data.energy_score;

                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            UnityPlayer.UnitySendMessage("HealthManager", "SetHealth", String.valueOf(score));
                        });
                    }
                }
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                e.printStackTrace();
            }
        }).start();

        return view;
    }

    @Override
    public void onDestroy() {
        mUnityPlayer.pause();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mUnityPlayer.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mUnityPlayer.resume();
        super.onResume();
    }
}
