package com.sh22.energy_saver_app.common;


import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ViewPortHandler;
import com.sh22.energy_saver_app.R;

import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.ApplianceCardData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Handler;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class ApplianceRecyclerViewAdapter extends RecyclerView.Adapter<ApplianceRecyclerViewAdapter.MyViewHolder> {
    static Context context;
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

        // Capitalise the first letter
        String pretty_name = appliance_data.get(position).getApplianceName();
        pretty_name = pretty_name.substring(0, 1).toUpperCase() + pretty_name.substring(1);
        // Multiple devices are concatenated together right now, so just take the first one
        if (pretty_name.contains("_")) {
            pretty_name = pretty_name.substring(0, pretty_name.indexOf("_"));
        }
        holder.DeviceTitle.setText(pretty_name);


        // Set the progress position and colour of the progress bar
        float appliance_score = SH22Utils.getEnergyScore(appliance_data, position);
        holder.progressBar.setProgress((int) (appliance_score * 100), true);
//        holder.progressBar.setProgress(32);

        int good_colour = ContextCompat.getColor(context, R.color.good_usage);
        int bad_colour = ContextCompat.getColor(context, R.color.bad_usage);
        int resultColor = ColorUtils.blendARGB(bad_colour, good_colour, appliance_score);
        holder.progressBar.setProgressTintList(ColorStateList.valueOf(resultColor));

        ArrayList<BarEntry> barEntriesArrayList = new ArrayList<>();
        barEntriesArrayList.add(new BarEntry(1f, appliance_data.get(position).getInitialUsage()));
        barEntriesArrayList.add(new BarEntry(2f, appliance_data.get(position).getUsageToday()));
        barEntriesArrayList.add(new BarEntry(3f, appliance_data.get(position).getNationalAverage()));
        BarDataSet barDataSet = new BarDataSet(barEntriesArrayList, "Bar Data Set");
        holder.barData = new BarData(barDataSet);
        holder.barChart.getXAxis().setDrawGridLines(false);
        holder.barChart.getAxisLeft().setDrawGridLines(true);
        holder.barChart.getAxisRight().setDrawGridLines(false);
        holder.barChart.getXAxis().setDrawAxisLine(false);
        holder.barChart.getAxisLeft().setDrawAxisLine(false);
        holder.barChart.getAxisRight().setDrawAxisLine(false);
        holder.barChart.getXAxis().setTextColor(Color.TRANSPARENT);
        holder.barChart.getAxisLeft().setTextColor(Color.TRANSPARENT);
        holder.barData.setDrawValues(false);
        holder.barChart.getDescription().setEnabled(false);
        holder.barChart.setData(holder.barData);
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);


        Legend legend = holder.barChart.getLegend();

        legend.setCustom(new LegendEntry[]{new LegendEntry("Initial Usage", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.PASTEL_COLORS[0]),
                new LegendEntry("Current Usage", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.PASTEL_COLORS[1]),
                new LegendEntry("National Average", Legend.LegendForm.SQUARE, 10f, 2f, null, ColorTemplate.PASTEL_COLORS[2])});
        //move the label to the top left
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//stack the labels

        legend.setEnabled(true);


        //When the tips button is clicked the tips button changes colour
        holder.TipsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                int color = context.getResources().getColor(R.color.blue_whale);
                holder.TipsButton.setBackgroundColor(color);
                holder.TipsButton.setTextColor(Color.WHITE);
                holder.BreakDownButton.setBackgroundColor(Color.WHITE);
                holder.BreakDownButton.setTextColor(color);
                holder.barChart.setVisibility(View.GONE);
                holder.tipsCard.setVisibility(View.VISIBLE);
                // Fill tips card with tip
                new Thread(() -> {
                    try {
                        String tip = BackendInterface.GetApplianceTip(context, appliance_data.get(position).getApplianceName());
                        new Handler(Looper.getMainLooper()).post(() -> {
                            holder.tipsText.setText(tip);
                        });
                    } catch (AuthenticationException e) {
                        SH22Utils.Logout(context);
                        e.printStackTrace();
                    } catch (BackendException e) {
                        SH22Utils.ToastException(view.getContext(), e.reason);
                        e.printStackTrace();
                    }
                }).start();

            }
        });


    }


    @Override
    public int getItemCount() {
        // Get how many items there are in total
        return appliance_data.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {


        TextView DeviceTitle;
        ProgressBar progressBar;
        Button InvisibleButton;
        TextView InfoText;
        CardView DeviceCard;
        Button BreakDownButton;
        Button TipsButton;
        CardView card1;
        CardView card2;
        CardView tipsCard;
        ImageView cautionLevel;
        BarChart barChart;
        BarData barData;
        BarDataSet barDataSet;
        ArrayList barEntriesArrayList;
        Button dropdownButton;
        Button button;
        TextView tipsText;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};

            DeviceCard = itemView.findViewById(R.id.card_view);
            DeviceTitle = itemView.findViewById(R.id.device_name);
            progressBar = itemView.findViewById(R.id.appliance_usage_bar);
            InvisibleButton = itemView.findViewById(R.id.device_name_button);
            dropdownButton = itemView.findViewById(R.id.dd);

            TipsButton = itemView.findViewById(R.id.button);
            BreakDownButton = itemView.findViewById(R.id.button2);
            InfoText = itemView.findViewById(R.id.info);

            card1 = itemView.findViewById(R.id.stat1);
            card2 = itemView.findViewById(R.id.stat2);
            cautionLevel = itemView.findViewById(R.id.home_icon2);
            barChart = itemView.findViewById(R.id.idBarChart);
            tipsCard = itemView.findViewById(R.id.tips);
            button = itemView.findViewById(R.id.button1);
            tipsText = itemView.findViewById(R.id.tip1);


            final boolean[] isExpanded = {false};
            final boolean[] isCompareExpanded = {false};
            int cardHeightChange = SH22Utils.dpToPixels(itemView.getContext(), 430);

            // Set an OnClickListener for the button
            InvisibleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (!isExpanded[0]) {


                        isExpanded[0] = true;


                        // Set the start and end values for the height and width animations
                        int initialHeight = DeviceCard.getHeight();
                        int finalHeight = initialHeight + cardHeightChange;
                        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(BreakDownButton, "alpha", 0, 1);
                        fadeInAnimator.setDuration(1000); // 1 second
                        fadeInAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                BreakDownButton.setVisibility(View.VISIBLE);


                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // Optional: add code to run when the animation is canceled
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // Optional: add code to run when the animation repeats
                            }
                        });


                        ValueAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(TipsButton, "alpha", 0, 1);
                        fadeInAnimator2.setDuration(1000); // 1 second
                        fadeInAnimator2.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                TipsButton.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Optional: add code to run when the animation ends
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                // Optional: add code to run when the animation is canceled
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                                // Optional: add code to run when the animation repeats
                            }
                        });


                        // Create ValueAnimator objects to animate the height and width
                        ValueAnimator heightAnimator = ValueAnimator.ofInt(initialHeight, finalHeight);

                        barChart.setVisibility(View.VISIBLE);
                        barChart.animateXY(500, 500);

                        isCompareExpanded[0] = true;
                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();

                        BreakDownButton.setBackgroundResource(R.drawable.rounded_rectangle_button);

                        Context context = view.getContext();
                        int color = context.getResources().getColor(R.color.white);
                        BreakDownButton.setTextColor(color);
                        // Set the duration and interpolator for the animations
                        heightAnimator.setDuration(200);
                        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        // Set update listeners on the animators to update the layout parameters as the animations progress
                        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.height = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });


                        // Create an AnimatorSet to run the height and width animations together
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(heightAnimator, fadeInAnimator, fadeInAnimator2);

                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                BreakDownButton.setClickable(true);
                                InvisibleButton.setClickable(true);

                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                BreakDownButton.setClickable(false);
                                InvisibleButton.setClickable(false);
                            }
                        });


                        animatorSet.start();


                    } else {


                        isExpanded[0] = false;

                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();
                        barChart.setVisibility(View.GONE);
                        tipsCard.setVisibility(View.GONE);
                        BreakDownButton.setBackgroundResource(R.drawable.rounded_rectangle_button2);

                        Context context = view.getContext();
                        int color = context.getResources().getColor(R.color.blue_whale);
                        BreakDownButton.setTextColor(color);


                        // Set the start and end values for the height and width animations
                        int initialHeight = DeviceCard.getHeight();
                        int finalHeight = initialHeight - cardHeightChange;


                        // Create ValueAnimator objects to animate the height and width
                        ValueAnimator heightAnimator = ValueAnimator.ofInt(initialHeight, finalHeight);


                        // Set the duration and interpolator for the animations
                        heightAnimator.setDuration(200);
                        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        // Set update listeners on the animators to update the layout parameters as the animations progress
                        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.height = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });


                        // Create an AnimatorSet to run the height and width animations together
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(heightAnimator);
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                BreakDownButton.setClickable(true);
                                InvisibleButton.setClickable(true);

                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                BreakDownButton.setClickable(false);
                                InvisibleButton.setClickable(false);
                            }
                        });

                        // Start the animation
                        animatorSet.start();

                        BreakDownButton.setVisibility(View.GONE);
                        TipsButton.setVisibility(View.GONE);

                    }
                }
            });


            BreakDownButton.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       Context context = view.getContext();
                                                       int color = context.getResources().getColor(R.color.blue_whale);
                                                       BreakDownButton.setBackgroundColor(color);
                                                       BreakDownButton.setTextColor(Color.WHITE);
                                                       TipsButton.setBackgroundColor(Color.WHITE);
                                                       TipsButton.setTextColor(color);
                                                       tipsCard.setVisibility(View.GONE);
                                                       barChart.setVisibility(View.VISIBLE);


                                                   }

                                               }
            );


        }
    }
}






