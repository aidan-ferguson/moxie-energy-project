package com.sh22.energy_saver_app.common;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendInterface;

public class FriendRequest {
    public UserInfo userInfo;

    public FriendRequest(int user_id, String first_name, String last_name) {
        userInfo = new UserInfo();
        userInfo.user_id = user_id;
        userInfo.firstname = first_name;
        userInfo.surname = last_name;
    }

    public void acceptRequest(Context context) {
        try {
            boolean ret_val = BackendInterface.AcceptFriendRequest(context, this.userInfo.user_id);
            Log.d("SH22", String.valueOf(ret_val));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }

    public void denyRequest(Context context) {
        try {
            boolean ret_val = BackendInterface.DenyFriendRequest(context, this.userInfo.user_id);
            Log.d("SH22", String.valueOf(ret_val));
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }
}
