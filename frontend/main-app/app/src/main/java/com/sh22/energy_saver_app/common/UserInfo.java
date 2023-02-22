package com.sh22.energy_saver_app.common;

public class UserInfo {
    public int user_id;
    public String username;
    public String firstname;
    public String surname;

    public UserInfo(int _user_id, String _username, String _firstname, String _surname) {
        user_id = _user_id;
        username = _username;
        firstname = _firstname;
        surname = _surname;
    }

    public UserInfo() {

    }
}
