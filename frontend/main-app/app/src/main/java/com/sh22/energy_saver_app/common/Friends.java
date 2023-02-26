package com.sh22.energy_saver_app.common;

import java.util.ArrayList;

public class Friends {
    public ArrayList<FriendRelationship> friends = new ArrayList<>();
    public ArrayList<FriendRequest> requests = new ArrayList<>();

    public Friends(ArrayList<FriendRelationship> friends, ArrayList<FriendRequest> requests) {
        this.friends = friends;
        this.requests = requests;
    }
}
