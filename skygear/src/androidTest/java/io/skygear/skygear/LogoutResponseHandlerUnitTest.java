package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LogoutResponseHandlerUnitTest {
    @Test
    @SmallTest
    public void testLogoutResponseHandlerSuccessFlow() throws Exception {
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
            }

            @Override
            public void onLogoutFail(String reason) {
                fail("Should not get error callback");
            }
        };

        logoutResponseHandler.onSuccess(new JSONObject());
    }

    @Test
    @SmallTest
    public void testLogoutResponseHandlerErrorFlow() throws Exception {
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                fail("Should not get success callback");
            }

            @Override
            public void onLogoutFail(String reason) {
                assertEquals("Test Error", reason);
            }
        };

        logoutResponseHandler.onFail(new Request.Error("Test Error"));
    }

    @Test
    @SmallTest
    public void testLogoutResponseHandlerAuthResolverFlow() throws Exception {
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
            }

            @Override
            public void onLogoutFail(String reason) {
                fail("Should not get error callback");
            }
        };

        logoutResponseHandler.authResolver = new AuthResolver() {
            @Override
            public void resolveAuthToken(String token) {
                assertNull(token);
            }
        };

        logoutResponseHandler.onSuccess(new JSONObject());
    }
}
