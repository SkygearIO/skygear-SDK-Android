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
                checkpoints[0] = true;
                assertEquals("user_001", user.userId);
                assertEquals("my-token", user.accessToken);
            }

            @Override
            public void onAuthFail(String reason) {
                fail("Should not get error callback");
            }
        };

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");
        data.put("user_id", "user_001");

        authResponseHandler.onSuccess(data);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAuthResponseHandlerFailFlow() throws Exception {
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                fail("Should not get success callback");
            }

            @Override
            public void onAuthFail(String reason) {
                assertEquals("Test error", reason);
            }
        };

        authResponseHandler.onFail(new Request.Error("Test error"));
    }
}
