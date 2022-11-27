package com.sh22.energy_saver_app;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.sh22.energy_saver_app.backendhandler.ApplianceCardData;

import java.util.ArrayList;
import java.util.Locale;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class ApplianceRecyclerViewAdapter extends RecyclerView.Adapter<ApplianceRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<ApplianceCardData> appliance_data;

    public ApplianceRecyclerViewAdapter(Context context, ArrayList<ApplianceCardData> appliance_data) {
        this.context = context;
        this.appliance_data = appliance_data;
    }

    @NonNull
    @Override
    public ApplianceRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout and assign the view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.appliance_row, parent, false);
        return new ApplianceRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplianceRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Assigning values to the already created views
        // Capitalise the first letter
        String pretty_name = appliance_data.get(position).getApplianceName();
        pretty_name = pretty_name.substring(0, 1).toUpperCase() + pretty_name.substring(1);
        // Multiple devices are concatenated together right now, so just take the first one
        if(pretty_name.contains("_")) {
            pretty_name = pretty_name.substring(0, pretty_name.indexOf("_"));
        }
        holder.textViewName.setText(pretty_name);

        // Set the progress position and colour of the progress bar
        holder.progressBar.setProgress((int) (appliance_data.get(position).getUsageToday() * 100), true);
        int good_colour = ContextCompat.getColor(context, R.color.good_usage);
        int bad_colour = ContextCompat.getColor(context, R.color.bad_usage);
        int resultColor = ColorUtils.blendARGB(good_colour, bad_colour, appliance_data.get(position).getUsageToday());
        holder.progressBar.setProgressTintList(ColorStateList.valueOf(resultColor));
    }

    @Override
    public int getItemCount() {
        // Get how many items there are in total
        return appliance_data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.device_name);
            progressBar = itemView.findViewById(R.id.appliance_usage_bar);
        }
    }
}
