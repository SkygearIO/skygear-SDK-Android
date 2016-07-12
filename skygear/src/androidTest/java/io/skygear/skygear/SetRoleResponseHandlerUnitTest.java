package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SetRoleResponseHandlerUnitTest {

    @Test
    public void testSetRoleResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        SetRoleResponseHandler handler = new SetRoleResponseHandler() {
            @Override
            public void onSetSuccess(Role[] roles) {
                assertEquals(2, roles.length);
                List<Role> roleList = Arrays.asList(roles);

                assertTrue(roleList.contains(new Role("God")));
                assertTrue(roleList.contains(new Role("Boss")));

                checkpoints[0] = true;
            }

            @Override
            public void onSetFail(String reason) {
                fail("Should not get error callback");
            }
        };

        JSONObject result = new JSONObject("{\"result\":[\"God\",\"Boss\"]}");
        handler.onSuccess(result);

        assertTrue(checkpoints[0]);
    }

    @Test
    public void testSetRoleResponseHandlerErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        SetRoleResponseHandler handler = new SetRoleResponseHandler() {
            @Override
            public void onSetSuccess(Role[] roles) {
                fail("Should not get success callback");
            }

            @Override
            public void onSetFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}