package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UnregisterDeviceRequestUnitTest {
    @Test
    public void testUnregisterDeviceRequestCreateFlow() throws Exception {
        UnregisterDeviceRequest request = new UnregisterDeviceRequest("device_1");

        assertEquals("device:unregister", request.action);

        Map<String, Object> data = request.data;
        assertEquals("device_1", data.get("id"));
    }
}
