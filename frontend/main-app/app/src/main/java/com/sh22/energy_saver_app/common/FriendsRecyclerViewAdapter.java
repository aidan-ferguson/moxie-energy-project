package com.sh22.energy_saver_app.common;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;

import java.util.ArrayList;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.MyViewHolder>  {
    static Context context;
    static ArrayList<FriendRequest> requests;

    public FriendsRecyclerViewAdapter(Context context, ArrayList<FriendRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public FriendsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout and assign the view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.request_row, parent, false);
        return new FriendsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsRecyclerViewAdapter.MyViewHolder holder, int position) {

        // Capitalise the first letter
        String pretty_name = requests.get(position).userInfo.firstname;

        holder.personName.setText(pretty_name);



        // Set the progress position and colour of the progress bar

//



    }

    @Override
    public int getItemCount() {
        return requests.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //name of requestee
        TextView personName;
        Button accept;
        Button decline;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};
            personName=itemView.findViewById(R.id.recent_invitation);
            accept=itemView.findViewById(R.id.accept);
            decline=itemView.findViewById(R.id.decline);

            accept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //accept the freidns request
                    new Thread(() -> {


                        requests.get(getAdapterPosition()).acceptRequest(context);

                    }


                    ).start();
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Thread(() -> {


                        requests.get(getAdapterPosition()).denyRequest(context);

                    }


                    ).start();
                }
            });



        }


        //set onclick fo
    }}


