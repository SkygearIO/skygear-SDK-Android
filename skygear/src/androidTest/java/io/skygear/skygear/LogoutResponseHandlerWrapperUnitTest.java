package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LogoutResponseHandlerWrapperUnitTest {
    @Test
    public void testLogoutResponseHandlerWrapperSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                checkpoints[0] = true;
            }

            @Override
            public void onLogoutFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        LogoutResponseHandlerWrapper wrapper = new LogoutResponseHandlerWrapper(
                null,
                logoutResponseHandler
        );

        wrapper.onSuccess(new JSONObject());

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testLogoutResponseHandlerWrapperFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LogoutResponseHandler logoutResponseHandler = new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                fail("Should not get success callback");
            }

            @Override
            public void onLogoutFail(String reason) {
                checkpoints[0] = true;
                assertEquals("Test Error", reason);
            }
        };

        LogoutResponseHandlerWrapper wrapper = new LogoutResponseHandlerWrapper(
                null,
                logoutResponseHandler
        );

        wrapper.onFail(new Request.Error("Test Error"));

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testLogoutResponseHandlerWrapperAuthResolveFlow() throws Exception {
        final boolean[] checkpoints = { false };
        AuthResolver resolver = new AuthResolver() {
            @Override
            public void resolveAuthUser(User user) {
                checkpoints[0] = true;
                assertNull(user);
            }
        };

        LogoutResponseHandlerWrapper wrapper = new LogoutResponseHandlerWrapper(resolver, null);
        wrapper.onSuccess(new JSONObject());

        assertTrue(checkpoints[0]);
    }
}
