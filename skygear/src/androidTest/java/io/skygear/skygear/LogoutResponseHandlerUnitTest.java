package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LogoutResponseHandlerUnitTest {
    @Test
    public void testLogoutResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                checkpoints[0] = true;
            }

            @Override
            public void onLogoutFail(String reason) {
                fail("Should not get error callback");
            }
        };

        logoutResponseHandler.onSuccess(new JSONObject());

        assertTrue(checkpoints[0]);
    }

    @Test
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
}
