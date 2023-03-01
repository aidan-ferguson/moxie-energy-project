package com.sh22.energy_saver_app.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.ui.activites.MainActivity;
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
        View view = inflater.inflate(R.layout.fragment_ecosystem, container, false);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#62A526"));
        ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<div><font color='#FFFFFF'>Your Ecosystem</font></div>"));
        ((MainActivity)getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MainActivity)getActivity()).getterActionBar().setCustomView(R.layout.action_bar_ecosystem);

        this.frameLayoutForUnity = view.findViewById(R.id.frameLayoutForUnity);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
