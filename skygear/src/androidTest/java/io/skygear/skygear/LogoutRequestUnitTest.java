package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LogoutRequestUnitTest {
    @Test
    public void testLogoutRequestNormalFlow() throws Exception {
        LogoutRequest req = new LogoutRequest();
        assertEquals("auth:logout", req.action);
    }
}
