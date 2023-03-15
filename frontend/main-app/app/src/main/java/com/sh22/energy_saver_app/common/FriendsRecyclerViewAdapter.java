package com.sh22.energy_saver_app.common;



import static com.sh22.energy_saver_app.common.ApplianceRecyclerViewAdapter.context;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.sh22.energy_saver_app.ui.activites.MainActivity;
import com.sh22.energy_saver_app.ui.fragments.HomeFragment;

import java.util.ArrayList;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

/**
 * RecyclerView for the friend requests page
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
 */
public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.MyViewHolder> {
    private final Context mContext;
    private final ArrayList<FriendRequest> mRequests;
    public static final String FRIEND_REQUEST_ACCEPTED_ACTION = "com.sh22.energy_saver_app.friend_request_accepted";

    public FriendsRecyclerViewAdapter(Context context, ArrayList<FriendRequest> requests) {
        mContext = context;
        mRequests = requests;
    }
    public interface OnAcceptButtonClickListener {
        void onAcceptButtonClicked(FriendRelationship friend);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.request_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Populate the name of the person requesting
        String firstname = mRequests.get(position).userInfo.firstname;
        String surname = mRequests.get(position).userInfo.surname;
        String full_name = firstname + " " + surname;
        holder.personName.setText(full_name);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    /**
     * View holder for the FriendsRecyclerView adapter
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView personName;
        Button accept;
        Button decline;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            personName = itemView.findViewById(R.id.recent_invitation);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);



            accept.setOnClickListener(v -> {
                // Accept the friend's request
                new Thread(() -> {
                    int position = getAdapterPosition();
                    mRequests.get(position).acceptRequest(mContext);
                    ((FragmentActivity) mContext).runOnUiThread(() -> {
                        // Remove the accepted friend request from the data list
                        mRequests.remove(position);

                        // Notify the adapter that the item has been removed
                        notifyItemRemoved(position);

                        // Notify the adapter that the data set has changed
                        notifyItemRangeChanged(position, mRequests.size());

                        // Send a broadcast to inform that a friend request has been accepted
                        Intent intent = new Intent(FRIEND_REQUEST_ACCEPTED_ACTION);
                        mContext.sendBroadcast(intent);
                    });
                }).start();

            });

            decline.setOnClickListener(v -> {
                // Accept the friend's request
                new Thread(() -> {
                    int position = getAdapterPosition();
                    mRequests.get(position).denyRequest(mContext);
                    ((FragmentActivity) mContext).runOnUiThread(() -> {
                        // Remove the accepted friend request from the data list
                        mRequests.remove(position);

                        // Notify the adapter that the item has been removed
                        notifyItemRemoved(position);

                        // Notify the adapter that the data set has changed
                        notifyItemRangeChanged(position, mRequests.size());

                        //update the active friends leaderboard recyclerView




                    });
                }).start();
            });





}


    }

    }