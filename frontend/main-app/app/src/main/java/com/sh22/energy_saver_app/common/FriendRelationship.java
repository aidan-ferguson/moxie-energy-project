package com.sh22.energy_saver_app.common;

public class FriendRelationship {
    public UserInfo userInfo;

    public FriendRelationship(int user_id, String first_name, String last_name) {
        userInfo = new UserInfo();
        userInfo.user_id = user_id;
        userInfo.firstname = first_name;
        userInfo.surname = last_name;
    }
}
