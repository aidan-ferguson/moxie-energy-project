package com.sh22.energy_saver_app.common;

/**
 * Contains a relationship between the current user and a friend, this holds all the information
 *   about the current user and is used to display information on both FriendsRecyclerViewAdapter
 */
public class FriendRelationship {
    public UserInfo userInfo;

    public FriendRelationship(int user_id, String first_name, String last_name, Double energy_score) {
        userInfo = new UserInfo();
        userInfo.user_id = user_id;
        userInfo.firstname = first_name;
        userInfo.surname = last_name;
        userInfo.energy_score = energy_score;
    }
}
