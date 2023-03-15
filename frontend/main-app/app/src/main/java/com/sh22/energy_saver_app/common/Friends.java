package com.sh22.energy_saver_app.common;

import java.util.ArrayList;

/**
 * Utility class for holding the current array of friends with and requests to the current user
 */
public class Friends {
    public ArrayList<FriendRelationship> friends;
    public ArrayList<FriendRequest> requests;

    public Friends(ArrayList<FriendRelationship> friends, ArrayList<FriendRequest> requests) {
        this.friends = friends;
        this.requests = requests;
    }
}
