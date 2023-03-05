package com.sh22.energy_saver_app.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.FriendRelationship;
import com.sh22.energy_saver_app.common.FriendRequest;
import com.sh22.energy_saver_app.common.Friends;
import com.sh22.energy_saver_app.common.SH22Utils;
import com.sh22.energy_saver_app.common.UserInfo;
import com.sh22.energy_saver_app.ui.activites.MainActivity;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

public void increaseHeight(View view){
    CardView card;
    CardView card2;

    card= view.findViewById(R.id.score_card);
    card2 = view.findViewById(R.id.center_card);
    ViewGroup.LayoutParams layoutParams = card2.getLayoutParams();

    int newWidth = card2.getWidth()+150;
    int newHeight = card2.getHeight()*2-200;
    ValueAnimator heightAnimator = ValueAnimator.ofInt(card2.getHeight(), newHeight);
    ValueAnimator widthAnimator = ValueAnimator.ofInt(card2.getWidth(), newWidth);

    heightAnimator.setDuration(200);
    heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    widthAnimator.setDuration(200);
    widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

    heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            layoutParams.height = (Integer) animation.getAnimatedValue();
            card2.setLayoutParams(layoutParams);
        }
    });

    widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            layoutParams.width = (Integer) animation.getAnimatedValue();
            card2.setLayoutParams(layoutParams);
        }
    });
    card.setVisibility(View.GONE);
    //Set constraints of card2 to the center of the screen


    AnimatorSet animatorSet = new AnimatorSet();
    animatorSet.playTogether(heightAnimator, widthAnimator);
    animatorSet.start();


}


    public void decreaseHeight(View view){
        CardView card;
        CardView card2;

        card= view.findViewById(R.id.score_card);
        card2 = view.findViewById(R.id.center_card);
        ViewGroup.LayoutParams layoutParams = card2.getLayoutParams();

        int newWidth = card2.getWidth()-150;
        int newHeight = SH22Utils.dpToPixels(view.getContext(), 350);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(card2.getHeight(), newHeight);
        ValueAnimator widthAnimator = ValueAnimator.ofInt(card2.getWidth(), newWidth);

        heightAnimator.setDuration(200);
        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimator.setDuration(200);
        widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.height = (Integer) animation.getAnimatedValue();
                card2.setLayoutParams(layoutParams);
            }
        });

        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams.width = (Integer) animation.getAnimatedValue();
                card2.setLayoutParams(layoutParams);
            }
        });
        card.setVisibility(View.VISIBLE);
        //Set constraints of card2 to the center of the screen


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(heightAnimator, widthAnimator);
        animatorSet.start();


    }
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#62A526"));
        ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#FFFFFF'>Welcome</font></div></center>"));

        ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#DEB276'>Welcome</font></div></center>"));
        ((MainActivity)getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MainActivity)getActivity()).getterActionBar().setCustomView(R.layout.action_bar);

        // Network calls are ordered by what will be the quickest

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                ApplianceData appliance_data = BackendInterface.get_appliance_data(view.getContext());
                if(appliance_data == null) {
                    Log.e("SH22", "Error loading appliance data");
                    throw new IOException();
                }

                // When we get the data, update the UI
                // Some time may have passed so we need to check if the activity is now null
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Currently the score will be the daily aggregate as a percentage of some number
                        float score = appliance_data.energy_score;//SH22Utils.getEnergyScore(appliance_data, "aggregate");

                        int progress = Math.round(score * 100) - 50;

                        int bad_colour = ContextCompat.getColor(activity, R.color.bad_usage);
                        int mid_colour = ContextCompat.getColor(activity, R.color.mid_usage);
                        int good_colour = ContextCompat.getColor(activity, R.color.good_usage);

                        int resultColor = 0;

                        if(score < 0.5)
                        {
                            resultColor = ColorUtils.blendARGB(mid_colour, bad_colour, score);
                        }
                        else
                        {
                            resultColor = ColorUtils.blendARGB(mid_colour, good_colour, score);
                        }



                        HalfGauge gauge = view.findViewById(R.id.halfGauge);
                        gauge.setMinValue(-100);
                        gauge.setMaxValue(100);
                        gauge.setValue(progress);
                        for(int i = -50; i < 50; i++)
                        {
                            Range range = new Range();
                            int colorToSet = ColorUtils.blendARGB(bad_colour, mid_colour, (i+50)/100f);
                            range.setColor(colorToSet);
                            range.setFrom(i-50);
                            range.setTo(i-49);
                            gauge.addRange(range);
                        }

                        for(int i = -50; i < 50; i++)
                        {
                            Range range = new Range();
                            int colorToSet = ColorUtils.blendARGB(mid_colour, good_colour, (i+50)/100f);
                            range.setColor(colorToSet);
                            range.setFrom(i+50);
                            range.setTo(i+49);
                            gauge.addRange(range);
                        }

                        TextView letterGrade = view.findViewById(R.id.home_fragment_letter_gradex);
                        if(progress <= 50)
                        {
                            letterGrade.setText("F-");
                        }
                        else if (progress <= 40)
                        {
                            letterGrade.setText("F+");
                        }
                        else if(progress <= -30)
                        {
                            letterGrade.setText("D-");
                        }
                        else if(progress <= -20)
                        {
                            letterGrade.setText("D+");
                        }
                        else if (progress <= -10)
                        {
                            letterGrade.setText("C-");
                        }
                        else if (progress <= 10)
                        {
                            letterGrade.setText("C+");
                        }
                        else if (progress <= 20)
                        {
                            letterGrade.setText("B-");
                        }
                        else if (progress <= 30)
                        {
                            letterGrade.setText("B+");
                        }
                        else if (progress > 40)
                        {
                            letterGrade.setText("A-");
                        }
                        else if (progress > 50)
                        {
                            letterGrade.setText("A+");
                        }
                        letterGrade.setTextColor(resultColor);
                    });
                }
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (IOException e) {
                SH22Utils.ToastException(view.getContext(), Constants.INTERNAL_ERROR);
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }).start();

        // Get user info to display on homepage
        new Thread(() -> {
            try {
                UserInfo userInfo = BackendInterface.GetUserInfo(view.getContext());
                if(userInfo != null) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {

                            ((MainActivity)getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
                            ((MainActivity)getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#FFFFFF'>Welcome, " + userInfo.firstname + "</font></div></center>"));
                        });
                    }
                }
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }).start();

        // Start new thread to get tips and usage report, these may take a while given OpenAI's inference time
        new Thread(() -> {
            try {
                String totd = BackendInterface.GetTOTD(view.getContext());
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Tip of the day
                        //TextView textView = view.findViewById(R.id.text_view);
                        //textView.setText(totd + "\n\n\n");
                    });
                }
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }).start();

        new Thread(() -> {
            try {
                String energy_report = BackendInterface.GetEnergyReport(view.getContext());
                FragmentActivity activity = getActivity();
                if(activity != null) {
                    activity.runOnUiThread(() -> {
                        // Usage report
                        //TextView textView2 = view.findViewById(R.id.text_view2);
                        //textView2.setText(energy_report + "\n\n\n");
                        //textView2.setGravity(Gravity.START);
                    });
                }
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }).start();

        new Thread(() -> {
            try {
                Friends friends = BackendInterface.GetFriends(view.getContext());
                // TODO: put friends somewhere
            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }).start();


        Button button1;
        Button button2;
        Button button3;
        Button button4;
        ImageView icon1;
        ImageView icon2;
        ImageView icon3;
        ImageView icon4;
        TextView label1;
        TextView label2;
        TextView label3;
        TextView label4;
        CardView card;
        CardView card2;

        card= view.findViewById(R.id.score_card);
        card2 = view.findViewById(R.id.center_card);


        TextView TipOfTheDay;
        TextView Tip;
        Button back1;

        TextView EnergyReport;
        ScrollView scrollView;
        TextView EnergyReportText;
        Button back2;

        TextView Krew;
        RecyclerView KrewView;
        Button back3;

        //Tip of the day elements -small view
        button1= view.findViewById(R.id.button1);
        icon1= view.findViewById(R.id.icon1);
        label1= view.findViewById(R.id.button1Text);

        //Tip of the day elements -big view
        TipOfTheDay= view.findViewById(R.id.title1);
        Tip= view.findViewById(R.id.textView3);
        back1= view.findViewById(R.id.dd1);

        //Energy report elements -small view
        button2= view.findViewById(R.id.button2);
        icon2= view.findViewById(R.id.icon2);
        label2= view.findViewById(R.id.button2Text);

        //Energy report elements -big view
        EnergyReport= view.findViewById(R.id.title2);
        scrollView= view.findViewById(R.id.report_scroll);
        EnergyReportText= view.findViewById(R.id.energy_report);
        back2= view.findViewById(R.id.dd2);

       //Krew elements -small view
        button3= view.findViewById(R.id.button3);
        icon3= view.findViewById(R.id.icon3);
        label3= view.findViewById(R.id.button3Text);

        //Krew elements -big view
        Krew= view.findViewById(R.id.title3);
        KrewView= view.findViewById(R.id.krew_recycler);
        back3= view.findViewById(R.id.dd3);






        View parent = view.findViewById(R.id.center_card);

    //method that increases the size of the card to be used in onclick functions




        button1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                ViewGroup.LayoutParams layoutParams = button1.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (button1.getWidth() * 2) + parent.getWidth() - amount - (2 * button1.getWidth())+150;
                int newHeight = card.getHeight()*2-100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button1.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button1.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button1.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button1.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                animatorSet.start();
                increaseHeight(view);





                button2.setVisibility(View.GONE);
                button3.setVisibility(View.GONE);


                icon2.setVisibility(View.GONE);
                icon3.setVisibility(View.GONE);


                label2.setVisibility(View.GONE);
                label3.setVisibility(View.GONE);

                label1.setVisibility(View.VISIBLE);
                icon1.setVisibility(View.GONE);


                label1.setVisibility(View.GONE);
                icon1.setVisibility(View.GONE);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(Tip, "alpha", 0, 1);
                fadeInAnimator.setDuration(1000); // 1 second
                ObjectAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(TipOfTheDay, "alpha", 0, 1);
                fadeInAnimator2.setDuration(1000); // 1 second


                fadeInAnimator.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {
                        Tip.setVisibility(View.VISIBLE);


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
                fadeInAnimator2.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {

                        TipOfTheDay.setVisibility(View.VISIBLE);

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

                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet.playTogether(fadeInAnimator, fadeInAnimator2);
                back1.setVisibility(View.VISIBLE);
                // Start the animation
                animatorSet.start();

                button1.setClickable(false);


            }
        });


        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setBackground(getResources().getDrawable(R.drawable.layout_bg3));


                button1.setClickable(true);
                ViewGroup.LayoutParams layoutParams = button1.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = button2.getWidth();
                int newHeight = button2.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button1.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button1.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button1.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button1.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                decreaseHeight(view);
                animatorSet.start();


                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);


                icon2.setVisibility(View.VISIBLE);
                icon3.setVisibility(View.VISIBLE);


                label2.setVisibility(View.VISIBLE);
                label3.setVisibility(View.VISIBLE);

                label1.setVisibility(View.VISIBLE);
                icon1.setVisibility(View.VISIBLE);


                label1.setVisibility(View.VISIBLE);
                icon1.setVisibility(View.VISIBLE);
                Tip.setVisibility(View.GONE);
                TipOfTheDay.setVisibility(View.GONE);
                back1.setVisibility(View.GONE);
            }
        });















        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ViewGroup.LayoutParams layoutParams = button2.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (button2.getWidth()*2)+ parent.getWidth()-amount-(2*button2.getWidth())+150;
                int newHeight = card.getHeight()*2-100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button2.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button2.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button2.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button2.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);
                increaseHeight(view);
                // Start the animation
                animatorSet.start();


                button1.setVisibility(View.GONE);
                                button3.setVisibility(View.GONE);


                                icon1.setVisibility(View.GONE);
                                icon3.setVisibility(View.GONE);


                                label1.setVisibility(View.GONE);
                                label3.setVisibility(View.GONE);


                label2.setVisibility(View.GONE);
                icon2.setVisibility(View.GONE);
                button2.setClickable(false);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(EnergyReport, "alpha", 0, 1);
                fadeInAnimator.setDuration(600); // 1 second
                ObjectAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(scrollView, "alpha", 0, 1);
                fadeInAnimator2.setDuration(600); // 1 second


                fadeInAnimator.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {
                        EnergyReport.setVisibility(View.VISIBLE);



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
                fadeInAnimator2.addListener(new Animator.AnimatorListener(){
                    @Override
                    public void onAnimationStart(Animator animation) {
                        scrollView.setVisibility(View.VISIBLE);
                        back2.setVisibility(View.VISIBLE);


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

                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet.playTogether(fadeInAnimator, fadeInAnimator2);

                // Start the animation
                animatorSet.start();


            }
        });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = button2.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = button1.getWidth();
                int newHeight = button1.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button2.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button2.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button2.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button2.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                decreaseHeight(view);
                animatorSet.start();


                button1.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);


                icon1.setVisibility(View.VISIBLE);
                icon3.setVisibility(View.VISIBLE);


                label1.setVisibility(View.VISIBLE);
                label3.setVisibility(View.VISIBLE);

                label2.setVisibility(View.VISIBLE);
                icon2.setVisibility(View.VISIBLE);


                label2.setVisibility(View.VISIBLE);
                icon2.setVisibility(View.VISIBLE);
                EnergyReport.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                back2.setVisibility(View.GONE);
                button2.setClickable(true);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup.LayoutParams layoutParams = button3.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (button1.getWidth() * 2) + parent.getWidth() - amount - (2 * button1.getWidth())+150;
                int newHeight = card.getHeight()*2-100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button3.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button3.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button3.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button3.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                increaseHeight(view);
                animatorSet.start();


                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);


                icon1.setVisibility(View.GONE);
                icon2.setVisibility(View.GONE);

                icon3.setVisibility(View.GONE);


                label1.setVisibility(View.GONE);
                label2.setVisibility(View.GONE);

                label3.setVisibility(View.GONE);
                back3.setVisibility(View.VISIBLE);

                Krew.setVisibility(View.VISIBLE);
                KrewView.setVisibility(View.VISIBLE);

                button3.setClickable(false);



            }
        });


        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button3.setBackground(getResources().getDrawable(R.drawable.layout_bg3));


                button3.setClickable(true);
                ViewGroup.LayoutParams layoutParams = button3.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = button2.getWidth();
                int newHeight = button2.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(button3.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(button3.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        button3.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        button3.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                decreaseHeight(view);
                animatorSet.start();


                button2.setVisibility(View.VISIBLE);
                button1.setVisibility(View.VISIBLE);

                button3.setVisibility(View.VISIBLE);

                icon2.setVisibility(View.VISIBLE);
                icon1.setVisibility(View.VISIBLE);
;
                icon3.setVisibility(View.VISIBLE);

                label2.setVisibility(View.VISIBLE);
                label1.setVisibility(View.VISIBLE);

                label3.setVisibility(View.VISIBLE);

                label1.setVisibility(View.VISIBLE);
                icon1.setVisibility(View.VISIBLE);
                Krew.setVisibility(View.GONE);
                KrewView.setVisibility(View.GONE);
                back3.setVisibility(View.GONE);
            }
        });


        // Return the inflated view
        return view;
    }



}