package com.sh22.energy_saver_app;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.sh22.energy_saver_app.common.UserInfo;

public class TestUserInfo {
    @Test
    public void test_set_and_get() {
        UserInfo userInfo = new UserInfo(1, "username", "firstname", "surname", "data_provider", 0.0);
        assertEquals(1, userInfo.user_id);
        assertEquals("username", userInfo.username);
        assertEquals("firstname", userInfo.firstname);
        assertEquals( "surname", userInfo.surname);
        assertEquals("data_provider", userInfo.data_provider);
        assertEquals(0.0, userInfo.energy_score, 0.0001);
    }
}
