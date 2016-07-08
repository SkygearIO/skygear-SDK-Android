package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class UserSaveResponseHandlerUnitTest {
    @Test
    public void testUserSaveResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserSaveResponseHandler handler = new UserSaveResponseHandler() {
            @Override
            public void onSaveSuccess(User user) {
                assertEquals("001", user.getId());
                assertEquals("user01@skygear.dev", user.getEmail());
                assertEquals("user01", user.getUsername());

                assertTrue(user.hasRole(new Role("Citizen")));
                assertTrue(user.hasRole(new Role("Programmer")));

                checkpoints[0] = true;
            }

            @Override
            public void onSaveFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        JSONObject userObject = new JSONObject();
        userObject.put("_id", "001");
        userObject.put("email", "user01@skygear.dev");
        userObject.put("username", "user01");
        userObject.put("roles", new JSONArray("[\"Citizen\", \"Programmer\"]"));

        handler.onSuccess(userObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testUserSaveResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserSaveResponseHandler handler = new UserSaveResponseHandler() {
            @Override
            public void onSaveSuccess(User user) {
                fail("Should not get success callback");
            }

            @Override
            public void onSaveFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}
