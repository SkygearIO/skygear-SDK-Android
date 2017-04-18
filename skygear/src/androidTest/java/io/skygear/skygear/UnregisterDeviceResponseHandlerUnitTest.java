package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UnregisterDeviceResponseHandlerUnitTest {
    @Test
    public void testUnregisterDeviceResponseSuccessFlow() throws Exception {
        JSONObject result = new JSONObject();
        result.put("id", "device_1");

        final boolean[] checkpoints = new boolean[] { false };
        UnregisterDeviceResponseHandler handler = new UnregisterDeviceResponseHandler() {
            @Override
            public void onUnregisterSuccess(String deviceId) {
                assertEquals("device_1", deviceId);
                checkpoints[0] = true;
            }

            @Override
            public void onUnregisterError(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(result);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testUnregisterDeviceResponseFailFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        UnregisterDeviceResponseHandler handler = new UnregisterDeviceResponseHandler() {
            @Override
            public void onUnregisterSuccess(String deviceId) {
                fail("Should not get success callback");
            }

            @Override
            public void onUnregisterError(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
