package com.sh22.energy_saver_app.common;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sh22.energy_saver_app.R;

import java.util.ArrayList;

// Good tutorial https://www.youtube.com/watch?v=Mc0XT58A1Z4

public class ActiveFriendsRecyclerViewAdapter extends RecyclerView.Adapter<ActiveFriendsRecyclerViewAdapter.MyViewHolder>  {
    static Context context;
    static ArrayList<FriendRelationship> friends;

    public ActiveFriendsRecyclerViewAdapter(Context context, ArrayList<FriendRelationship> friends) {
        this.context = context;
        this.friends = friends;
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
        String pretty_name = friends.get(position).userInfo.firstname;


        holder.personName.setText(pretty_name);



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




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            final String[] message = {null};
            personName=itemView.findViewById(R.id.name);




        }


        //set onclick fo
    }}


