package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class UserUnitTest {
    @Test
    public void testUserModelNormalFlow() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");

        assertEquals("user_id_001", user.id);
        assertEquals("my-token", user.accessToken);
        assertEquals("user_001", user.username);
        assertEquals("user001@skygear.dev", user.email);
    }

    @Test
    public void testUserModelWithoutUsernameEmail() throws Exception {
        User user = new User("user_id_001", "my-token");

        assertEquals("user_id_001", user.id);
        assertEquals("my-token", user.accessToken);
        assertNull(user.username);
        assertNull(user.email);
    }
}
