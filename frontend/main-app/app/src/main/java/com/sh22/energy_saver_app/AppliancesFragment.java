package com.sh22.energy_saver_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sh22.energy_saver_app.backendhandler.ApplianceCardData;
import com.sh22.energy_saver_app.backendhandler.ApplianceData;
import com.sh22.energy_saver_app.backendhandler.BackendInterface;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;


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

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data();
                // When we get the data, update the UI

                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Construct the list of ApplianceCardData
                        ArrayList<ApplianceCardData> appliances = new ArrayList<ApplianceCardData>();
                        for(int idx = 0; idx < appliance_data.labels.size(); idx++){
                            // For now we just divide by some big number if the appliance is not the
                            //   aggregate
                            if(!appliance_data.labels.get(idx).equals("aggregate")) {
                                ApplianceCardData new_data = new ApplianceCardData(
                                        appliance_data.labels.get(idx),
                                        (float) (appliance_data.today.get(idx) / 50),
                                        (float) (appliance_data.weekly_average.get(idx) / 50)
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

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
        return view;
    }
}