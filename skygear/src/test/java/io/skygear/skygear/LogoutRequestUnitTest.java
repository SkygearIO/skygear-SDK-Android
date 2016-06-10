package io.skygear.skygear;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LogoutRequestUnitTest {
    @Test
    public void testLogoutRequestNormalFlow() throws Exception {
        LogoutRequest req = new LogoutRequest();
        assertEquals("auth:logout", req.action);
    }
}
