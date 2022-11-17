package com.sh22.energy_saver_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.unity3d.player.UnityPlayer;

public class EcosystemFragment extends Fragment {
    // Eco score variables [0, 1]
    private static final String ARG_PARAM1 = "score";
    private String score = "0.0";

    // Unity player variables
    protected UnityPlayer mUnityPlayer;
    FrameLayout frameLayoutForUnity;

    public EcosystemFragment() {
    }

    public static EcosystemFragment newInstance(String score) {
        Bundle bundle = new Bundle();
        bundle.putString("score", score.toString());
        EcosystemFragment fragment = new EcosystemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            score = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Create unity player and view
        mUnityPlayer = new UnityPlayer(getActivity());
        View view = inflater.inflate(R.layout.fragment_ecosystem, container, false);
        this.frameLayoutForUnity = (FrameLayout) view.findViewById(R.id.frameLayoutForUnity);
        this.frameLayoutForUnity.addView(mUnityPlayer.getView(),
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        // Initialise view
        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the eco-score of the player
        UnityPlayer.UnitySendMessage("HealthManager", "SetHealth", score);
        return view;
    }

    @Override
    public void onDestroy() {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }
}
