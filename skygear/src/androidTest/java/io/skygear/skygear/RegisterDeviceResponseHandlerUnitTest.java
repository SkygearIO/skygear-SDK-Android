package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RegisterDeviceResponseHandlerUnitTest {
    @Test
    public void testRegisterDeviceResponseHandlerSuccessFlow() throws Exception {
        JSONObject result = new JSONObject();
        result.put("id", "test-device-id");

        final boolean[] checkpoints = new boolean[] { false };
        RegisterDeviceResponseHandler handler = new RegisterDeviceResponseHandler() {
            @Override
            public void onRegisterSuccess(String deviceId) {
                assertEquals("test-device-id", deviceId);
                checkpoints[0] = true;
            }

            @Override
            public void onRegisterError(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(result);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRegisterDeviceResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        RegisterDeviceResponseHandler handler = new RegisterDeviceResponseHandler() {
            @Override
            public void onRegisterSuccess(String deviceId) {
                fail("Should not get success callback");
            }

            @Override
            public void onRegisterError(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
