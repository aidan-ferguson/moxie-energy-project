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
import com.sh22.energy_saver_app.ui.activites.MainActivity;
import com.sh22.energy_saver_app.ui.fragments.HomeFragment;

import java.util.ArrayList;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class FriendsRecyclerViewAdapter extends RecyclerView.Adapter<FriendsRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<FriendRequest> mRequests;

    public FriendsRecyclerViewAdapter(Context context, ArrayList<FriendRequest> requests) {
        mContext = context;
        mRequests = requests;
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
        String firstname = mRequests.get(position).userInfo.firstname;
        String surname = mRequests.get(position).userInfo.surname;
        holder.personName.setText(firstname+ " " + surname);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

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
                    mRequests.get(getAdapterPosition()).acceptRequest(mContext);
                    ((FragmentActivity) mContext).runOnUiThread(() -> {

                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f);
                        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        alphaAnimator.setDuration(500);
                        alphaAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {

                                notifyDataSetChanged();
                            }
                            @Override
                            public void onAnimationCancel(Animator animator) {}
                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        });
                        alphaAnimator.start();



                    });
                }).start();
            });

            decline.setOnClickListener(v -> {
                // Decline the friend's request
                new Thread(() -> {
                    mRequests.get(getAdapterPosition()).denyRequest(mContext);
                    ((FragmentActivity) mContext).runOnUiThread(() -> {


                        //decline


                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f);
                        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        alphaAnimator.setDuration(500);
                        alphaAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {}
                            @Override
                            public void onAnimationEnd(Animator animation) {

                                notifyDataSetChanged();
                            }
                            @Override
                            public void onAnimationCancel(Animator animator) {}
                            @Override
                            public void onAnimationRepeat(Animator animator) {}
                        });
                        alphaAnimator.start();
                    });
                }).start();
            });
        }
    }
}