package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AuthResponseHandlerWrapperUnitTest {

    @Test
    public void testAuthResponseHandlerWrapperSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(String token) {
                checkpoints[0] = true;
                assertEquals("my-token", token);
            }

            @Override
            public void onAuthFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        AuthResponseHandlerWrapper wrapper = new AuthResponseHandlerWrapper(
                null,
                authResponseHandler
        );

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");

        wrapper.onSuccess(data);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAuthResponseHandlerWrapperFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(String token) {
                fail("Should not get success callback");
            }

            @Override
            public void onAuthFail(String reason) {
                checkpoints[0] = true;
                assertEquals("Test Error", reason);
            }
        };

        AuthResponseHandlerWrapper wrapper = new AuthResponseHandlerWrapper(
                null,
                authResponseHandler
        );

        wrapper.onFail(new Request.Error("Test Error"));

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testAuthResponseHandlerWrapperAuthResolveFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResolver resolver = new AuthResolver() {
            @Override
            public void resolveAuthToken(String token) {
                checkpoints[0] = true;
                assertEquals("my-token", token);
            }
        };

        AuthResponseHandlerWrapper wrapper = new AuthResponseHandlerWrapper(resolver, null);

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");

        wrapper.onSuccess(data);

        assertTrue(checkpoints[0]);
    }
}
