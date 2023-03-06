package com.sh22.energy_saver_app.common;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class ActiveFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ActiveFriendsRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<FriendRelationship> friends;

    public ActiveFriendsRecyclerViewAdapter(Context context, ArrayList<FriendRelationship> friends) throws AuthenticationException, BackendException {
        this.context = context;
        this.friends = friends;

        //add the data for the user that is logged in
        new Thread(() -> {
            try {
                UserInfo userInfo = BackendInterface.GetUserInfo(context);
                FriendRelationship friendRelationship = new FriendRelationship(userInfo.user_id, userInfo.firstname, userInfo.surname, userInfo.energy_score);
                friends.add(friendRelationship);

                //sort the friends array by the score
                friends.sort((o1, o2) -> o2.userInfo.energy_score.compareTo(o1.userInfo.energy_score));
            } catch (AuthenticationException e) {
                SH22Utils.Logout(context);
                e.printStackTrace();
            } catch (BackendException e) {
                SH22Utils.ToastException(context, e.reason);
                e.printStackTrace();
            }

        }).start();
    }

    // Add this method to update the friends list
    public void addFriend(FriendRelationship friend) {
        // Add the new friend to the list of friends
        friends.add(friend);

        // Sort the friends array by the score
        Collections.sort(friends, (o1, o2) -> o2.userInfo.energy_score.compareTo(o1.userInfo.energy_score));

        // Notify the adapter that the data has changed
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ActiveFriendsRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout and assign the view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_row, parent, false);
        return new ActiveFriendsRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveFriendsRecyclerViewAdapter.MyViewHolder holder, int position) {
        // Capitalise the first letter
        String pretty_name = friends.get(position).userInfo.firstname + " " + friends.get(position).userInfo.surname;

        holder.rank.setText(Integer.toString(position + 1));
        holder.personName.setText(pretty_name);

        //only get the first 3 digits of the score
        Double score = friends.get(position).userInfo.energy_score;//SH22Utils.getEnergyScore(appliance_data, "aggregate");
        int progress = (int) ((Math.round(score * 100) - 50) * 2);

        holder.personName.setTextColor(ContextCompat.getColor(context, R.color.black));


        try {
            if (friends.get(position).userInfo.user_id == BackendInterface.GetUserInfo(context).user_id) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#4877DD76"));
                holder.personName.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.rank.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.score.setTextColor(ContextCompat.getColor(context, R.color.black));
                //set to bold style text
                holder.score.setTypeface(null, Typeface.BOLD);
                holder.personName.setTypeface(null, Typeface.BOLD);
                holder.rank.setTypeface(null, Typeface.BOLD);

            }
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (BackendException e) {
            e.printStackTrace();
        }

        if (progress <= -50) {
            holder.score.setText("F-");
        } else if (progress <= -40) {
            holder.score.setText("F+");
        } else if (progress <= -30) {
            holder.score.setText("D-");
        } else if (progress <= -20) {
            holder.score.setText("D+");
        } else if (progress <= -10) {
            holder.score.setText("C-");
        } else if (progress <= 10) {
            holder.score.setText("C+");
        } else if (progress <= 20) {
            holder.score.setText("B-");
        } else if (progress <= 30) {
            holder.score.setText("B+");
        } else if (progress <= 40) {
            holder.score.setText("A-");
        } else if (progress >= 50) {
            holder.score.setText("A+");
        }


        // Set the progress position and colour of the progress bar

//


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //name of requestee
        TextView personName;
        TextView score;
        TextView rank;
        Integer id;
        CardView cardView;
        TextView your_id;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};
            personName = itemView.findViewById(R.id.name);
            score = itemView.findViewById(R.id.score);
            rank = itemView.findViewById(R.id.rank);
            cardView = itemView.findViewById(R.id.card_view);
            //get your_id_number


        }


        //set onclick fo
    }
}


