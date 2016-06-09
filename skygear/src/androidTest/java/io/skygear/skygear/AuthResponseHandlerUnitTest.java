package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class AuthResponseHandlerUnitTest {

    @Test
    @SmallTest
    public void testAuthResponseHandlerSuccessFlow() throws Exception {
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(String token) {
                assertEquals("my-token", token);
            }

            @Override
            public void onAuthFail(String reason) {
                fail("Should not get error callback");
            }
        };

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");

        authResponseHandler.onSuccess(data);
    }

    @Test
    @SmallTest
    public void testAuthResponseHandlerFailFlow() throws Exception {
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(String token) {
                fail("Should not get success callback");
            }

            @Override
            public void onAuthFail(String reason) {
                assertEquals("Test error", reason);
            }
        };

        authResponseHandler.onFail(new Request.Error("Test error"));
    }

    @Test
    @SmallTest
    public void testAuthResponseHandlerAuthResolverFlow() throws Exception {
        AuthResponseHandler authResponseHandler = new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(String token) {
                assertEquals("my-token", token);
            }

            @Override
            public void onAuthFail(String reason) {
                fail("Should not get error callback");
            }
        };

        authResponseHandler.authResolver = new AuthResolver() {
            @Override
            public void resolveAuthToken(String token) {
                assertEquals("my-token", token);
            }
        };

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");

        authResponseHandler.onSuccess(data);
    }
}
