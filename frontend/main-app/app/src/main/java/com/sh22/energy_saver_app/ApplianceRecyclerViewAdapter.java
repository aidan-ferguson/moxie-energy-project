package com.sh22.energy_saver_app;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

import android.graphics.Color;
import android.util.Size;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sh22.energy_saver_app.backendhandler.ApplianceCardData;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class ApplianceRecyclerViewAdapter extends RecyclerView.Adapter<ApplianceRecyclerViewAdapter.MyViewHolder>  {
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
        // Assigning values to the already created views
        // Capitalise the first letter
        String pretty_name = appliance_data.get(position).getApplianceName();
        pretty_name = pretty_name.substring(0, 1).toUpperCase() + pretty_name.substring(1);
        // Multiple devices are concatenated together right now, so just take the first one
        if(pretty_name.contains("_")) {
            pretty_name = pretty_name.substring(0, pretty_name.indexOf("_"));
        }
        holder.DeviceTitle.setText(pretty_name);

        // Set the font
        Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/MicroFLF.ttf");
        holder.DeviceTitle.setTypeface(type);

        // Set the progress position and colour of the progress bar

        //holder.progressBar.setProgress((int) (appliance_data.get(position).getUsageToday() * 100), true);
        holder.progressBar.setProgress(32);

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

        public void showPopUp(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("TIP");
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do something when the OK button is clicked
                }
            });
            builder.show();
        }

        TextView DeviceTitle;
        ProgressBar progressBar;
        Button InvisibleButton;
        TextView InfoText;
        CardView DeviceCard;
        Button CompareButton;
        Button TipsButton;
        CardView card1;
        CardView card2;
        ImageView cautionLevel;
        BarChart barChart;
        BarData barData;
        BarDataSet barDataSet;
        ArrayList barEntriesArrayList;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};

            DeviceCard = itemView.findViewById(R.id.card_view);
            DeviceTitle = itemView.findViewById(R.id.device_name);
            progressBar = itemView.findViewById(R.id.appliance_usage_bar);
            InvisibleButton = itemView.findViewById(R.id.device_name_button);

            TipsButton = itemView.findViewById(R.id.button);
            CompareButton= itemView.findViewById(R.id.button2);
            InfoText = itemView.findViewById(R.id.info);

            card1 = itemView.findViewById(R.id.stat1);
            card2 = itemView.findViewById(R.id.stat2);
            cautionLevel = itemView.findViewById(R.id.home_icon2);
            barChart = itemView.findViewById(R.id.idBarChart);




            final boolean[] isExpanded = {false};
            final boolean[] isCompareExpanded = {false};

            // Set an OnClickListener for the button
            InvisibleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    if (!isExpanded[0]) {

                        isExpanded[0] = true;



                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();


                        // Set the start and end values for the height and width animations
                        int initialHeight = DeviceCard.getHeight();;
                        int finalHeight = initialHeight + 700;
                        int initialWidth = DeviceCard.getWidth();;
                        int finalWidth = initialWidth + 30;

                        // Create ValueAnimator objects to animate the height and width
                        ValueAnimator heightAnimator = ValueAnimator.ofInt(initialHeight, finalHeight);
                        ValueAnimator widthAnimator = ValueAnimator.ofInt(initialWidth, finalWidth);

                        // Set the duration and interpolator for the animations
                        heightAnimator.setDuration(200);
                        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        widthAnimator.setDuration(200);
                        widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        // Set update listeners on the animators to update the layout parameters as the animations progress
                        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.height = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });
                        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.width = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });

                        // Create an AnimatorSet to run the height and width animations together


                        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(CompareButton, "alpha", 0, 1);
                        fadeInAnimator.setDuration(1000); // 1 second
                        fadeInAnimator.addListener(new Animator.AnimatorListener(){
                            @Override
                            public void onAnimationStart(Animator animation) {
                                CompareButton.setVisibility(View.VISIBLE);

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






                        ObjectAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(TipsButton, "alpha", 0, 1);
                        fadeInAnimator2.setDuration(1000); // 1 second
                        fadeInAnimator2.addListener(new Animator.AnimatorListener(){
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


                        Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/MicroFLF.ttf");
                        CompareButton.setTypeface(type);
                        TipsButton.setTypeface(type);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(heightAnimator, widthAnimator, fadeInAnimator, fadeInAnimator2);

                        // Start the animation
                        animatorSet.start();
                        card1.setVisibility(View.VISIBLE);
                        card2.setVisibility(View.VISIBLE);



                        cautionLevel.setScaleX(0.1f);
                        cautionLevel.setScaleY(0.1f);

// Create an ObjectAnimator to animate the scale of the image
                        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(cautionLevel, "scaleX", 0.1f, 1f);
                        scaleXAnimator.setDuration(300);
                        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(cautionLevel, "scaleY", 0.1f, 1f);
                        scaleYAnimator.setDuration(300);

// Set the initial state of the icon
                        cautionLevel.setRotation(0f);
                        cautionLevel.setAlpha(0f);

// Create an ObjectAnimator to animate the rotation of the icon
                        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(cautionLevel, "rotation", 0f, 360f);
                        rotationAnimator.setDuration(300);
                        rotationAnimator.setInterpolator(new LinearInterpolator());

// Create an ObjectAnimator to animate the alpha of the icon
                        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(cautionLevel, "alpha", 0f, 1f);
                        alphaAnimator.setDuration(500);

// Create an AnimatorSet to play the rotation and alpha animators together
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        animatorSet2.playTogether(rotationAnimator, alphaAnimator, scaleXAnimator, scaleYAnimator);

// Set the icon to visible and start the animator set
                        cautionLevel.setVisibility(View.VISIBLE);
                        animatorSet2.start();
                        DeviceCard.setCardElevation(22);


                    } else {


                        isExpanded[0] = false;

                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();

                        int initialHeight = DeviceCard.getHeight();
                        int finalHeight = initialHeight-700;
                        int initialWidth = DeviceCard.getWidth();
                        int finalWidth = initialWidth-30;

                        if (isCompareExpanded[0] == true){
                            initialHeight = DeviceCard.getHeight();
                            finalHeight = initialHeight-2100;
                            initialWidth = DeviceCard.getWidth();
                            finalWidth = initialWidth-30;
                            isCompareExpanded[0]=false;

                        }

                        // Create ValueAnimator objects to animate the height and width
                        ValueAnimator heightAnimator = ValueAnimator.ofInt(initialHeight, finalHeight);
                        ValueAnimator widthAnimator = ValueAnimator.ofInt(initialWidth, finalWidth);

                        // Set the duration and interpolator for the animations
                        heightAnimator.setDuration(100);
                        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                        widthAnimator.setDuration(100);
                        widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        // Set update listeners on the animators to update the layout parameters as the animations progress
                        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.height = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });
                        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int value = (int) animation.getAnimatedValue();
                                layoutParams.width = value;
                                DeviceCard.setLayoutParams(layoutParams);
                            }
                        });

                        // Create an AnimatorSet to run the height and width animations together
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(heightAnimator, widthAnimator);

                        // Start the animation
                        animatorSet.start();
                        CompareButton.setVisibility(View.GONE);
                        barChart.setVisibility(View.GONE);
                        TipsButton.setVisibility(View.GONE);
                        DeviceCard.setCardElevation(9);
                        card1.setVisibility(View.GONE);
                        card2.setVisibility(View.GONE);

                    }

                    ///OkHttpClient client = new OkHttpClient();

                    //Create a JSON object to send in the request body
                    /*JSONObject requestJson = new JSONObject();
                    try {
                        requestJson.put("model", "text-davinci-003");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        requestJson.put("prompt", " Give 1 tip for how to save money and energy when using a " + DeviceTitle.getText().toString().toLowerCase() );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        requestJson.put("max_tokens", 1024);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Create a RequestBody object with the JSON object as the content
                    RequestBody requestBody = RequestBody.create(requestJson.toString(), MediaType.parse("application/json; charset=utf-8"));

                    Request request = new Request.Builder()
                            .url("https://api.openai.com/v1/completions")
                            .post(requestBody)
                            .addHeader("Authorization", "Bearer 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")
                            .addHeader("Content-Type", "application/json")
                            .build();

                    // Make the request and get the response
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            InvisibleButton.setText("FAILED");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Do something with the response
                            //get the text from the response


                            /*String responseString = response.body().string();

                            JSONObject responseJson = null;
                            try {
                                responseJson = new JSONObject(responseString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            JSONArray choices = null;
                            try {
                                choices = responseJson.getJSONArray("choices");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            JSONObject firstChoice = null;
                            try {
                                firstChoice = choices.getJSONObject(0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String text = null;
                            try {
                                text = firstChoice.getString("text");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            // Set the text for a TextView

                            Log.d("myTag", responseString);
                            Log.d("myTag", text);
                            message[0] = text;
                            //show the pop up


                        }


                    });
                    //show pop up if api returns a response
                   */
                }
            });
            //When the user clicks on the compare button it will increase the height further in order to show more information

            CompareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isCompareExpanded[0]) {

                        getBarEntries();

                        // creating a new bar data set.
                        barDataSet = new BarDataSet(barEntriesArrayList, "Friends");

                        // creating a new bar data and
                        // passing our bar data set.
                        barData = new BarData(barDataSet);

                        // below line is to set data
                        // to our bar chart.
                        barChart.setData(barData);

                        // adding color to our bar data set.
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                        // setting text color.
                        barDataSet.setValueTextColor(Color.BLACK);

                        // setting text size
                        barDataSet.setValueTextSize(16f);
                        barChart.getDescription().setEnabled(false);
                        barChart.setVisibility(View.VISIBLE);
                        barChart.animateXY(500, 500);
                        isCompareExpanded[0] = true;
                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();

                        CompareButton.setBackgroundResource(R.drawable.rounded_rectangle_button);

                        Context context = view.getContext();
                        int color = context.getResources().getColor(R.color.white);
                        CompareButton.setTextColor(color);
                        CompareButton.setText("Collapse");

                        // Set the start and end values for the height and width animations
                        int initialHeight = DeviceCard.getHeight();
                        int finalHeight = initialHeight + 1400;
                        ;

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

                        // Start the animation
                        animatorSet.start();

                }
                    else{
                        isCompareExpanded[0] = false;

                        ViewGroup.LayoutParams layoutParams = DeviceCard.getLayoutParams();
                        barChart.setVisibility(View.GONE);
                        CompareButton.setBackgroundResource(R.drawable.rounded_rectangle_button2);

                        Context context = view.getContext();
                        int color = context.getResources().getColor(R.color.blue_whale);
                        CompareButton.setTextColor(color);
                        CompareButton.setText("COMPARE");

                        // Set the start and end values for the height and width animations
                        int initialHeight = DeviceCard.getHeight();
                        int finalHeight = initialHeight - 1400;
                        ;

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

                        // Start the animation
                        animatorSet.start();


                    }
            }

                                                 private void getBarEntries() {
                                                     barEntriesArrayList = new ArrayList<>();

                                                     // adding new entry to our array list with bar
                                                     // entry and passing x and y axis value to it.
                                                     barEntriesArrayList.add(new BarEntry(1f, 4));
                                                     barEntriesArrayList.add(new BarEntry(2f, 6));
                                                     barEntriesArrayList.add(new BarEntry(3f, 8));

                                                 }
                                             }





            );

            //When the tips button is clicked the tips button changes colour
            TipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


        }

    }
            );

            //define get fra
}}}





