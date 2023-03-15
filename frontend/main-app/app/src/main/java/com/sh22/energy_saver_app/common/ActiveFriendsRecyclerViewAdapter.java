package com.sh22.energy_saver_app.common;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.sh22.energy_saver_app.R;
import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;

import java.util.ArrayList;
import java.util.Collections;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

/**
 * RecyclerViewAdapter for the friends page
 * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
 */
public class ActiveFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ActiveFriendsRecyclerViewAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<FriendRelationship> friends;

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
    public void updateFriendsList(ArrayList<FriendRelationship> newFriends) {
        this.friends = newFriends;
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
        Double score = friends.get(position).userInfo.energy_score;
        int progress = (int) ((Math.round(score * 100) - 50) * 2);

        holder.personName.setTextColor(ContextCompat.getColor(context, R.color.forest_green));

        // Set the UI elements for the row
        try {
            if (friends.get(position).userInfo.user_id == BackendInterface.GetUserInfo(context).user_id) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#4877DD76"));
                holder.personName.setTextColor(ContextCompat.getColor(context, R.color.forest_green));
                holder.rank.setTextColor(ContextCompat.getColor(context, R.color.forest_green));
                holder.score.setTextColor(ContextCompat.getColor(context, R.color.forest_green));
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

        holder.score.setText(SH22Utils.getLetterGrade(progress));

        int resultColor = 0;
        int bad_colour = ContextCompat.getColor(context, R.color.bad_usage);
        int mid_colour = ContextCompat.getColor(context, R.color.mid_usage);
        int good_colour = ContextCompat.getColor(context, R.color.good_usage);
        if(score < 0.5)
        {
            resultColor = ColorUtils.blendARGB(bad_colour, mid_colour, score.floatValue());
        }
        else
        {
            resultColor = ColorUtils.blendARGB(mid_colour, good_colour, score.floatValue());
        }
        holder.score.setTextColor(resultColor);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Holds references to the UI elements in the friends card
        TextView personName;
        TextView score;
        TextView rank;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};
            personName = itemView.findViewById(R.id.name);
            score = itemView.findViewById(R.id.score);
            rank = itemView.findViewById(R.id.rank);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}