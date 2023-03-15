package com.sh22.energy_saver_app.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.common.ActiveFriendsRecyclerViewAdapter;
import com.sh22.energy_saver_app.common.ApplianceData;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendInterface;
import com.sh22.energy_saver_app.common.Constants;
import com.sh22.energy_saver_app.common.Friends;
import com.sh22.energy_saver_app.common.FriendsRecyclerViewAdapter;
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


    public void decreaseHeight(View view) {
        CardView card;
        CardView card2;

        card = view.findViewById(R.id.score_card);
        card2 = view.findViewById(R.id.center_card);
        ViewGroup.LayoutParams layoutParams = card2.getLayoutParams();

        int newWidth = card2.getWidth() - 150;
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
        ((MainActivity) getActivity()).getterActionBar().setBackgroundDrawable(colorDrawable);
        ((MainActivity) getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#FFFFFF'>Welcome</font></div></center>"));

        ((MainActivity) getActivity()).getterActionBar().setTitle(Html.fromHtml("<center><div><font color='#DEB276'>Welcome</font></div></center>"));
        ((MainActivity) getActivity()).getterActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((MainActivity) getActivity()).getterActionBar().setCustomView(R.layout.action_bar);


        // Network calls are ordered by what will be the quickest

        // Await appliance data coming in and update the page accordingly
        new Thread(() -> {
            try {
                int o = BackendInterface.GetUserInfo(view.getContext()).user_id;
                // interger to string

                ApplianceData appliance_data = BackendInterface.get_appliance_data(view.getContext());
                if (appliance_data == null) {
                    Log.e("SH22", "Error loading appliance data");
                    throw new IOException();
                }

                // When we get the data, update the UI
                // Some time may have passed so we need to check if the activity is now null
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        // Currently the score will be the daily aggregate as a percentage of some number
                        float score = appliance_data.energy_score;//SH22Utils.getEnergyScore(appliance_data, "aggregate");
                        TextView v = view.findViewById(R.id.your_id_number);
                        int progress = (Math.round(score * 100) - 50) * 2;

                        int bad_colour = ContextCompat.getColor(activity, R.color.bad_usage);
                        int mid_colour = ContextCompat.getColor(activity, R.color.mid_usage);
                        int good_colour = ContextCompat.getColor(activity, R.color.good_usage);
                        v.setText(Integer.toString(o));
                        int resultColor = 0;

                        if (score < 0.5) {
                            resultColor = ColorUtils.blendARGB(bad_colour, mid_colour, score);
                        } else {
                            resultColor = ColorUtils.blendARGB(mid_colour, good_colour, score);
                        }


                        HalfGauge gauge = view.findViewById(R.id.halfGauge);
                        gauge.setMinValue(-100);
                        gauge.setMaxValue(100);
                        gauge.setValue(progress);
                        for (int i = -50; i < 50; i++) {
                            Range range = new Range();
                            int colorToSet = ColorUtils.blendARGB(bad_colour, mid_colour, (i + 50) / 100f);
                            range.setColor(colorToSet);
                            range.setFrom(i - 50);
                            range.setTo(i - 49);
                            gauge.addRange(range);
                        }

                        for (int i = -50; i < 50; i++) {
                            Range range = new Range();
                            int colorToSet = ColorUtils.blendARGB(mid_colour, good_colour, (i + 50) / 100f);
                            range.setColor(colorToSet);
                            range.setFrom(i + 50);
                            range.setTo(i + 49);
                            gauge.addRange(range);
                        }

                        TextView letterGrade = view.findViewById(R.id.home_fragment_letter_gradex);
                        if (progress <= -50) {
                            letterGrade.setText("F-");
                        } else if (progress <= -40) {
                            letterGrade.setText("F+");
                        } else if (progress <= -30) {
                            letterGrade.setText("D-");
                        } else if (progress <= -20) {
                            letterGrade.setText("D+");
                        } else if (progress <= -10) {
                            letterGrade.setText("C-");
                        } else if (progress <= 10) {
                            letterGrade.setText("C+");
                        } else if (progress <= 20) {
                            letterGrade.setText("B-");
                        } else if (progress <= 30) {
                            letterGrade.setText("B+");
                        } else if (progress <= 40) {
                            letterGrade.setText("A-");
                        } else if (progress > 40) {
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
                if (userInfo != null) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(() -> {
                            ((TextView) activity.findViewById(R.id.action_bar_title)).setText("Welcome, " + userInfo.firstname);
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
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        // Tip of the day
                        TextView textView = view.findViewById(R.id.tip_of_the_day);
                        textView.setText(totd);
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
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        // Usage report
                        TextView report_text_view = view.findViewById(R.id.energy_report);
                        report_text_view.setText(energy_report + "\n\n");
                        report_text_view.setGravity(Gravity.START);
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
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        // Now attach the recycler view class to the view in the layout
                        RecyclerView recyclerView = view.findViewById(R.id.request_recycler_view);
                        FriendsRecyclerViewAdapter adapter = new FriendsRecyclerViewAdapter(view.getContext(), friends.requests);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    });
                }

            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }


        ).start();

        new Thread(() -> {
            try {


                Friends friends = BackendInterface.GetFriends(view.getContext());
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        // Now attach the recycler view class to the view in the layout
                        RecyclerView recyclerView = view.findViewById(R.id.friends_recycler_view);
                        ActiveFriendsRecyclerViewAdapter adapter = null;
                        try {
                            adapter = new ActiveFriendsRecyclerViewAdapter(view.getContext(), friends.friends);
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                        } catch (BackendException e) {
                            e.printStackTrace();
                        }
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                    });
                }

            } catch (AuthenticationException e) {
                SH22Utils.Logout(view.getContext());
            } catch (BackendException e) {
                SH22Utils.ToastException(view.getContext(), e.reason);
            }
        }


        ).start();


        CardView card = view.findViewById(R.id.score_card);
        CardView card2 = view.findViewById(R.id.center_card);

        //Tip of the day elements -small view
        button1= view.findViewById(R.id.tip_square);
        icon1= view.findViewById(R.id.icon1);
        label1= view.findViewById(R.id.button1Text);

        //Tip of the day elements -big view
        TipOfTheDay= view.findViewById(R.id.title1);
        Tip= view.findViewById(R.id.tip_of_the_day);
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
        RecyclerView leaderboard= view.findViewById(R.id.friends_recycler_view);
        //get the request recycler view from the
        Button leaderboardButton = view.findViewById(R.id.leaderboard);
        Button requestsButton = view.findViewById(R.id.manage);

        //Leaderboard elements
        TextView Krew = view.findViewById(R.id.title3);
        TextView leaderboardtitle = view.findViewById(R.id.leaderboardtitle);
        RecyclerView leaderboard = view.findViewById(R.id.friends_recycler_view);

        //Requests elements
        RecyclerView requests = view.findViewById(R.id.request_recycler_view);
        Button back3 = view.findViewById(R.id.dd3);
        EditText send_request = view.findViewById(R.id.friend_id);
        TextView your_id = view.findViewById(R.id.your_id);
        TextView your_id_text = view.findViewById(R.id.your_id_number);
        Button send = view.findViewById(R.id.enter);


        View parent = view.findViewById(R.id.center_card);


        TipOfDayButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                ViewGroup.LayoutParams layoutParams = TipOfDayButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (TipOfDayButton.getWidth() * 2) + parent.getWidth() - amount - (2 * TipOfDayButton.getWidth()) + 150;
                int newHeight = card.getHeight() * 2 - 100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(TipOfDayButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(TipOfDayButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        TipOfDayButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        TipOfDayButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                animatorSet.start();
                increaseHeight(view);


                EnergyReportButton.setVisibility(View.GONE);
                KrewButton.setVisibility(View.GONE);


                EnergyReportImage.setVisibility(View.GONE);
                KrewImage.setVisibility(View.GONE);


                EnergyReportLabel.setVisibility(View.GONE);
                KrewLabel.setVisibility(View.GONE);

                TipOfDayLabel.setVisibility(View.VISIBLE);
                TipOfDayImage.setVisibility(View.GONE);


                TipOfDayLabel.setVisibility(View.GONE);
                TipOfDayImage.setVisibility(View.GONE);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(Tip, "alpha", 0, 1);
                fadeInAnimator.setDuration(1000); // 1 second
                ObjectAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(TipOfTheDay, "alpha", 0, 1);
                fadeInAnimator2.setDuration(1000); // 1 second


                fadeInAnimator.addListener(new Animator.AnimatorListener() {
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
                fadeInAnimator2.addListener(new Animator.AnimatorListener() {
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

                TipOfDayButton.setClickable(false);


            }
        });


        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipOfDayButton.setBackground(getResources().getDrawable(R.drawable.layout_bg3));


                TipOfDayButton.setClickable(true);
                ViewGroup.LayoutParams layoutParams = TipOfDayButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = EnergyReportButton.getWidth();
                int newHeight = EnergyReportButton.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(TipOfDayButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(TipOfDayButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        TipOfDayButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        TipOfDayButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                decreaseHeight(view);
                animatorSet.start();


                EnergyReportButton.setVisibility(View.VISIBLE);
                KrewButton.setVisibility(View.VISIBLE);


                EnergyReportImage.setVisibility(View.VISIBLE);
                KrewImage.setVisibility(View.VISIBLE);


                EnergyReportLabel.setVisibility(View.VISIBLE);
                KrewLabel.setVisibility(View.VISIBLE);

                TipOfDayLabel.setVisibility(View.VISIBLE);
                TipOfDayImage.setVisibility(View.VISIBLE);


                TipOfDayLabel.setVisibility(View.VISIBLE);
                TipOfDayImage.setVisibility(View.VISIBLE);
                Tip.setVisibility(View.GONE);
                TipOfTheDay.setVisibility(View.GONE);
                back1.setVisibility(View.GONE);
            }
        });


        EnergyReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ViewGroup.LayoutParams layoutParams = EnergyReportButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (EnergyReportButton.getWidth() * 2) + parent.getWidth() - amount - (2 * EnergyReportButton.getWidth()) + 150;
                int newHeight = card.getHeight() * 2 - 100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(EnergyReportButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(EnergyReportButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        EnergyReportButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        EnergyReportButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);
                increaseHeight(view);
                // Start the animation
                animatorSet.start();


                TipOfDayButton.setVisibility(View.GONE);
                KrewButton.setVisibility(View.GONE);


                TipOfDayImage.setVisibility(View.GONE);
                KrewImage.setVisibility(View.GONE);


                TipOfDayLabel.setVisibility(View.GONE);
                KrewLabel.setVisibility(View.GONE);


                EnergyReportLabel.setVisibility(View.GONE);
                EnergyReportImage.setVisibility(View.GONE);
                EnergyReportButton.setClickable(false);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(EnergyReport, "alpha", 0, 1);
                fadeInAnimator.setDuration(600); // 1 second
                ObjectAnimator fadeInAnimator2 = ObjectAnimator.ofFloat(scrollView, "alpha", 0, 1);
                fadeInAnimator2.setDuration(600); // 1 second


                fadeInAnimator.addListener(new Animator.AnimatorListener() {
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
                fadeInAnimator2.addListener(new Animator.AnimatorListener() {
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
                ViewGroup.LayoutParams layoutParams = EnergyReportButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = TipOfDayButton.getWidth();
                int newHeight = TipOfDayButton.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(EnergyReportButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(EnergyReportButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        EnergyReportButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        EnergyReportButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                decreaseHeight(view);
                animatorSet.start();


                TipOfDayButton.setVisibility(View.VISIBLE);
                KrewButton.setVisibility(View.VISIBLE);


                TipOfDayImage.setVisibility(View.VISIBLE);
                KrewImage.setVisibility(View.VISIBLE);


                TipOfDayLabel.setVisibility(View.VISIBLE);
                KrewLabel.setVisibility(View.VISIBLE);

                EnergyReportLabel.setVisibility(View.VISIBLE);
                EnergyReportImage.setVisibility(View.VISIBLE);


                EnergyReportLabel.setVisibility(View.VISIBLE);
                EnergyReportImage.setVisibility(View.VISIBLE);
                EnergyReport.setVisibility(View.GONE);
                scrollView.setVisibility(View.GONE);
                back2.setVisibility(View.GONE);
                EnergyReportButton.setClickable(true);
            }
        });

        KrewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ViewGroup.LayoutParams layoutParams = KrewButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = (TipOfDayButton.getWidth() * 2) + parent.getWidth() - amount - (2 * TipOfDayButton.getWidth()) + 150;
                int newHeight = card.getHeight() * 2 - 100;
                ValueAnimator heightAnimator = ValueAnimator.ofInt(KrewButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(KrewButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        KrewButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        KrewButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                increaseHeight(view);
                animatorSet.start();


                TipOfDayButton.setVisibility(View.GONE);
                EnergyReportButton.setVisibility(View.GONE);


                TipOfDayImage.setVisibility(View.GONE);
                EnergyReportImage.setVisibility(View.GONE);

                KrewImage.setVisibility(View.GONE);


                TipOfDayLabel.setVisibility(View.GONE);
                EnergyReportLabel.setVisibility(View.GONE);

                KrewLabel.setVisibility(View.GONE);
                back3.setVisibility(View.VISIBLE);

                Krew.setVisibility(View.VISIBLE);
                leaderboard.setVisibility(View.VISIBLE);
                requestsButton.setVisibility(View.VISIBLE);
                leaderboardButton.setVisibility(View.VISIBLE);
                leaderboardButton.setBackground(getResources().getDrawable(R.drawable.layout_bg6));
                requestsButton.setBackground(getResources().getDrawable(R.drawable.layout_bg7));
                KrewButton.setVisibility(View.GONE);
                leaderboardtitle.setVisibility(View.VISIBLE);
                leaderboardtitle.setText("Your Energy Leaderboard");
                leaderboardButton.setTextColor(Color.parseColor("#ffffff"));
                requestsButton.setTextColor(Color.parseColor("#62A526"));


                KrewButton.setClickable(false);


            }
        });


        back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KrewButton.setBackground(getResources().getDrawable(R.drawable.layout_bg3));


                KrewButton.setClickable(true);
                ViewGroup.LayoutParams layoutParams = KrewButton.getLayoutParams();
                Integer amount = SH22Utils.dpToPixels(view.getContext(), 64);
                int newWidth = EnergyReportButton.getWidth();
                int newHeight = EnergyReportButton.getHeight();
                ValueAnimator heightAnimator = ValueAnimator.ofInt(KrewButton.getHeight(), newHeight);
                ValueAnimator widthAnimator = ValueAnimator.ofInt(KrewButton.getWidth(), newWidth);

                heightAnimator.setDuration(200);
                heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                widthAnimator.setDuration(200);
                widthAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.height = value;
                        KrewButton.setLayoutParams(layoutParams);
                    }
                });
                widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        layoutParams.width = value;
                        KrewButton.setLayoutParams(layoutParams);
                    }
                });

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(heightAnimator, widthAnimator);

                // Start the animation
                decreaseHeight(view);
                animatorSet.start();


                EnergyReportButton.setVisibility(View.VISIBLE);
                TipOfDayButton.setVisibility(View.VISIBLE);

                KrewButton.setVisibility(View.VISIBLE);

                EnergyReportImage.setVisibility(View.VISIBLE);
                TipOfDayImage.setVisibility(View.VISIBLE);
                ;
                KrewImage.setVisibility(View.VISIBLE);

                EnergyReportLabel.setVisibility(View.VISIBLE);
                TipOfDayLabel.setVisibility(View.VISIBLE);

                KrewLabel.setVisibility(View.VISIBLE);

                TipOfDayLabel.setVisibility(View.VISIBLE);
                TipOfDayImage.setVisibility(View.VISIBLE);
                Krew.setVisibility(View.GONE);

                requests.setVisibility(View.VISIBLE);
                requestsButton.setVisibility(View.VISIBLE);
                leaderboardButton.setVisibility(View.VISIBLE);
                leaderboard.setVisibility(View.VISIBLE);
                back3.setVisibility(View.GONE);
                requestsButton.setVisibility(View.GONE);
                leaderboardButton.setVisibility(View.GONE);
                leaderboard.setVisibility(View.GONE);
                requests.setVisibility(View.GONE);
                leaderboardtitle.setVisibility(View.GONE);
                send_request.setVisibility(View.GONE);
                your_id.setVisibility(View.GONE);
                send.setVisibility(View.GONE);

            }
        });

        //onclick for when leaderboards is clicked
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();

// Refresh the ActiveFriendsRecyclerViewAdapter

                leaderboard.setVisibility(View.VISIBLE);
                requests.setVisibility(View.GONE);
                leaderboardtitle.setText("Your Energy Leaderboard");
                send_request.setVisibility(View.GONE);
                your_id.setVisibility(View.GONE);
                send.setVisibility(View.GONE);
                leaderboardButton.setBackground(getResources().getDrawable(R.drawable.layout_bg6));
                requestsButton.setBackground(getResources().getDrawable(R.drawable.layout_bg7));
                leaderboardButton.setTextColor(Color.parseColor("#ffffff"));
                your_id_text.setVisibility(View.GONE);
                //green (blue_jeans) = #2ECC71
                requestsButton.setTextColor(Color.parseColor("#62A526"));


            }
        });

        //onlick for when manage is clicked
        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requests.setVisibility(View.VISIBLE);
                leaderboard.setVisibility(View.GONE);
                leaderboardtitle.setText("Manage your Friends");
                send_request.setVisibility(View.VISIBLE);
                your_id.setVisibility(View.VISIBLE);
                your_id_text.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
                leaderboardButton.setBackground(getResources().getDrawable(R.drawable.layout_bg7));
                requestsButton.setBackground(getResources().getDrawable(R.drawable.layout_bg6));
                leaderboardButton.setTextColor(Color.parseColor("#62A526"));
                requestsButton.setTextColor(Color.parseColor("#ffffff"));

            }
        });


//set onclick for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                    //get the input from the edit text
                    Integer id = Integer.valueOf(send_request.getText().toString());
                    try {
                        BackendInterface.CreateFriendRequest(view.getContext(), id);
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    } catch (BackendException e) {
                        e.printStackTrace();
                    }
                    Activity activity = (Activity) view.getContext(); // get the activity associated with the current context
                    activity.runOnUiThread(() -> {
                        // Create a custom dialog
                        final Dialog dialog = new Dialog(view.getContext());
                        dialog.setContentView(R.layout.custom_dialog);
                        dialog.setTitle("Friend Request Sent");

                        // Set the text and button for the dialog
                        TextView text = dialog.findViewById(R.id.dialog_text);
                        text.setText("Your friend request has been sent.");
                        Button button = dialog.findViewById(R.id.dialog_button);
                        button.setOnClickListener(v1 -> {
                            dialog.dismiss();
                            send_request.setText("");
                            send_request.setHint("Enter your friend's ID");
                        });

                        // Show the dialog
                        dialog.show();
                    });
                }).start();
            }
            // Return the inflated view

        });


        return view;

    }
}