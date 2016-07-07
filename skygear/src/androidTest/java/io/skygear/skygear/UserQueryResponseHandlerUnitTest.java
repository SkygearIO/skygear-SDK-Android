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
public class UserQueryResponseHandlerUnitTest {
    @Test
    public void testUserQueryResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserQueryResponseHandler handler = new UserQueryResponseHandler() {
            @Override
            public void onQuerySuccess(User[] users) {
                assertEquals(2, users.length);

                assertEquals("001", users[0].getId());
                assertEquals("user01@skygear.dev", users[0].getEmail());
                assertEquals("user01", users[0].getUsername());

                assertEquals("002", users[1].getId());
                assertEquals("user02@skygear.dev", users[1].getEmail());
                assertEquals("user02", users[1].getUsername());

                checkpoints[0] = true;
            }

            @Override
            public void onQueryFail(String reason) {
                fail("Should not get error callback");
            }
        };

        JSONArray queryResult = new JSONArray();

        JSONObject dataObject = new JSONObject();
        dataObject.put("_id", "001");
        dataObject.put("email", "user01@skygear.dev");
        dataObject.put("username", "user01");

        JSONObject userObject = new JSONObject();
        userObject.put("data", dataObject);
        userObject.put("id", "001");
        userObject.put("type", "user");

        queryResult.put(userObject);

        dataObject = new JSONObject();
        dataObject.put("_id", "002");
        dataObject.put("email", "user02@skygear.dev");
        dataObject.put("username", "user02");

        userObject = new JSONObject();
        userObject.put("data", dataObject);
        userObject.put("id", "002");
        userObject.put("type", "user");

        queryResult.put(userObject);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", queryResult);

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testUserQueryResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        UserQueryResponseHandler handler = new UserQueryResponseHandler() {
            @Override
            public void onQuerySuccess(User[] users) {
                fail("Should not get success callback");
            }

            @Override
            public void onQueryFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test Error"));

        assertTrue(checkpoints[0]);
    }
}
