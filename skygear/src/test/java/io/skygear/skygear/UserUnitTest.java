package io.skygear.skygear;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserUnitTest {
    @Test
    public void testUserModelNormalFlow() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");

        assertEquals("user_id_001", user.userId);
        assertEquals("my-token", user.accessToken);
        assertEquals("user_001", user.username);
        assertEquals("user001@skygear.dev", user.email);
    }

    @Test
    public void testUserModelWithoutUsernameEmail() throws Exception {
        User user = new User("user_id_001", "my-token");

        assertEquals("user_id_001", user.userId);
        assertEquals("my-token", user.accessToken);
        assertNull(user.username);
        assertNull(user.email);
    }
}
