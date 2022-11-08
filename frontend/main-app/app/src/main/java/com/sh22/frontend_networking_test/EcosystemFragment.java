package com.sh22.frontend_networking_test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.unity3d.player.UnityPlayer;

public class EcosystemFragment extends Fragment {
    protected UnityPlayer mUnityPlayer;
    FrameLayout frameLayoutForUnity;

    public EcosystemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUnityPlayer = new UnityPlayer(getActivity());
        View view = inflater.inflate(R.layout.fragment_ecosystem, container, false);
        this.frameLayoutForUnity = (FrameLayout) view.findViewById(R.id.frameLayoutForUnity);
        this.frameLayoutForUnity.addView(mUnityPlayer.getView(),
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        mUnityPlayer.requestFocus();
        mUnityPlayer.windowFocusChanged(true);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
