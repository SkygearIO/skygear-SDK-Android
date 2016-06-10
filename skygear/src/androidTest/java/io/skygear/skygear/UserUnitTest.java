package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
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

    @Test
    public void testUserModelFromJsonNormalFlow() throws Exception {
        String json =
                "{" +
                "  \"user_id\": \"456\"," +
                "  \"access_token\": \"token_456\"," +
                "  \"username\": \"user_456\"," +
                "  \"email\": \"user456@skygear.dev\"" +
                "}";

        User user = User.fromJsonString(json);

        assertEquals("456", user.userId);
        assertEquals("token_456", user.accessToken);
        assertEquals("user_456", user.username);
        assertEquals("user456@skygear.dev", user.email);
    }

    @Test(expected = JSONException.class)
    public void testUserModelFromJsonNoUserIdFlow() throws Exception {
        String json =
                "{" +
                "  \"access_token\": \"token_456\"," +
                "  \"username\": \"user_456\"," +
                "  \"email\": \"user456@skygear.dev\"" +
                "}";

        User.fromJsonString(json);
    }

    @Test(expected = JSONException.class)
    public void testUserModelFromJsonNoAccessTokenFlow() throws Exception {
        String json =
                "{" +
                "  \"user_id\": \"456\"," +
                "  \"username\": \"user_456\"," +
                "  \"email\": \"user456@skygear.dev\"" +
                "}";

        User.fromJsonString(json);
    }

    @Test
    public void testUserModelToJsonNormalFlow() throws Exception {
        User user = new User("user_id_001", "my-token", "user_001", "user001@skygear.dev");
        String jsonString = user.toJsonString();
        JSONObject json = new JSONObject(jsonString);

        assertEquals("user_id_001", json.getString("user_id"));
        assertEquals("my-token", json.getString("access_token"));
        assertEquals("user_001", json.getString("username"));
        assertEquals("user001@skygear.dev", json.getString("email"));
    }
}
