package com.sh22.energy_saver_app.common;

import android.content.Context;
import android.util.Log;

import com.sh22.energy_saver_app.backend.AuthenticationException;
import com.sh22.energy_saver_app.backend.BackendException;
import com.sh22.energy_saver_app.backend.BackendInterface;

/**
 * For holding a FriendRequest from some user to the current user
 */
public class FriendRequest {
    public UserInfo userInfo;

    public FriendRequest(int user_id, String first_name, String last_name) {
        userInfo = new UserInfo();
        userInfo.user_id = user_id;
        userInfo.firstname = first_name;
        userInfo.surname = last_name;
    }

    /**
     * Function for accepting the current friend request instance
     * @param context - the current context of the application, used to access AuthorisationToken
     */
    public void acceptRequest(Context context) {
        try {
            boolean ret_val = BackendInterface.AcceptFriendRequest(context, this.userInfo.user_id);
            Log.d("SH22", String.valueOf(ret_val));
        } catch (AuthenticationException e) {
            SH22Utils.Logout(context);
        } catch (BackendException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for denying the current friend request instance
     * @param context - the current context of the application, used to access AuthorisationToken
     */
    public void denyRequest(Context context) {
        try {
            boolean ret_val = BackendInterface.DenyFriendRequest(context, this.userInfo.user_id);
            Log.d("SH22", String.valueOf(ret_val));
        } catch (AuthenticationException e) {
            SH22Utils.Logout(context);
        } catch (BackendException e) {
            e.printStackTrace();
        }
    }
}
