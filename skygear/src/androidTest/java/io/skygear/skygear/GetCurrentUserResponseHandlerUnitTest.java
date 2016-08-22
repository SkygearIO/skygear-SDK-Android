package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class GetCurrentUserResponseHandlerUnitTest {
    @Test
    public void testGetCurrentUserResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        GetCurrentUserResponseHandler handler = new GetCurrentUserResponseHandler() {
            @Override
            public void onGetCurrentUserSuccess(User user) {
                assertNotNull(user);
                assertEquals("user-id-1", user.id);
                assertEquals("user1", user.getUsername());
                assertEquals("user1@skygear.dev", user.getEmail());
                assertTrue(user.roles.contains(new Role("Developer")));
                assertTrue(user.roles.contains(new Role("Designer")));

                checkpoints[0] = true;
            }

            @Override
            public void onGetCurrentUserFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject(
                "{" +
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
    public void testGetCurrentUserResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        GetCurrentUserResponseHandler handler = new GetCurrentUserResponseHandler() {
            @Override
            public void onGetCurrentUserSuccess(User user) {
                fail("Should not get success callback");
            }

            @Override
            public void onGetCurrentUserFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
