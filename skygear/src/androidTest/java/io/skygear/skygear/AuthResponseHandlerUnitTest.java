package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AuthResponseHandlerUnitTest {

    @Test
    public void testAuthResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                assertEquals("my-token", user.accessToken);
                assertEquals("user-id-1", user.id);
                assertEquals("user1", user.getUsername());
                assertEquals("user1@skygear.dev", user.getEmail());
                assertTrue(user.roles.contains(new Role("Developer")));
                assertTrue(user.roles.contains(new Role("Designer")));

                checkpoints[0] = true;
            }

            @Override
            public void onAuthFail(String reason) {
                fail("Should not get error callback");
            }
        };

        authResponseHandler.onSuccess(new JSONObject(
                "{" +
                "  \"access_token\": \"my-token\"," +
                "  \"user_id\": \"user-id-1\"," +
                "  \"username\": \"user1\"," +
                "  \"email\": \"user1@skygear.dev\"," +
                "  \"roles\": [" +
                "    \"Developer\"," +
                "    \"Designer\"" +
                "  ]" +
                "}"
        ));

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAuthResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                fail("Should not get success callback");
            }

            @Override
            public void onAuthFail(String reason) {
                assertEquals("Test error", reason);
                checkpoints[0] = true;
            }
        };

        authResponseHandler.onFail(new Request.Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
