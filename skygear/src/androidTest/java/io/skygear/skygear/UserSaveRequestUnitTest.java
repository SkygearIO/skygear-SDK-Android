package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UserSaveRequestUnitTest {
    @Test
    public void testUserSaveRequestCreationFlow() throws Exception {
        User user = new User("123", "token_123", "user123", "user123@skygear.dev");
        user.addRole(new Role("Citizen"));
        user.addRole(new Role("Programmer"));

        UserSaveRequest request = new UserSaveRequest(user);
        assertEquals("user:update", request.action);

        Map<String, Object> data = request.data;
        assertEquals("123", data.get("_id"));
        assertEquals("user123@skygear.dev", data.get("email"));

        List<String> roleNameList = Arrays.asList((String[]) data.get("roles"));
        assertTrue(roleNameList.contains("Citizen"));
        assertTrue(roleNameList.contains("Programmer"));
    }
}
