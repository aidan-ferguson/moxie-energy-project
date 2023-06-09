package com.sh22.energy_saver_app.common;

/**
 * Utility class for holding all the required information for any user, can be friends or the
 *   current user
 */
public class UserInfo {
    public  int user_id;
    public String username;
    public String firstname;
    public String surname;
    public String data_provider;
    public Double energy_score;

    public UserInfo(int _user_id, String _username, String _firstname, String _surname, String _data_provider, Double _energy_score) {
        user_id = _user_id;
        username = _username;
        firstname = _firstname;
        surname = _surname;
        data_provider = _data_provider;
        energy_score = _energy_score;
    }

    public UserInfo() {

    }



}
