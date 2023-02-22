package com.sh22.energy_saver_app.ui.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.common.ApplianceRecyclerViewAdapter;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.common.ApplianceCardData;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.ui.activites.MainActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AppliancesFragment extends Fragment {


    public AppliancesFragment() {
        // Required empty public constructor
    }

    public static AppliancesFragment newInstance(String param1, String param2) {
        AppliancesFragment fragment = new AppliancesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appliances, container, false);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#04244C"));
        ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#DEB276'>Appliances</font></div></center>"));
        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data(view.getContext());
                Map<String, Double> national_averages = BackendInterface.GetNationalAverages();

                // When we get the data, update the UI
                if(appliance_data != null && national_averages != null) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            // Construct the list of ApplianceCardData
                            ArrayList<ApplianceCardData> appliances = new ArrayList<ApplianceCardData>();
                            for (int idx = 0; idx < appliance_data.labels.size(); idx++) {
                                // For now we just divide by some big number if the appliance is not the
                                //   aggregate
                                if (!appliance_data.labels.get(idx).equals("aggregate")) {
                                    Double national_average = national_averages.getOrDefault(appliance_data.labels.get(idx), 0.0);
                                    if(national_average == null){
                                        national_average = 0.0;
                                    }
                                    ApplianceCardData new_data = new ApplianceCardData(
                                            appliance_data.labels.get(idx),
                                            appliance_data.initial_usage.get(idx).floatValue(),
                                            appliance_data.today.get(idx).floatValue(),
                                            appliance_data.weekly_average.get(idx).floatValue(),
                                            national_average.floatValue()
                                    );
                                    appliances.add(new_data);
                                }
                            }

                            // Now attach the recycler view class to the view in the layout
                            RecyclerView recyclerView = activity.findViewById(R.id.appliance_recycler_view);
                            ApplianceRecyclerViewAdapter adapter = new ApplianceRecyclerViewAdapter(activity, appliances);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));


                        });
                    }
                }

            } catch (AuthenticationException e) {
                e.printStackTrace();
            }
        }).start();
        return view;
    }
}