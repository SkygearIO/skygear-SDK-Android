package io.skygear.skygear;

import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LoginRequestUnitTest {
    @Test
    public void testLoginRequestUsernameFlow() throws Exception {
        LoginRequest req = new LoginRequest("user123", null, "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:login", req.action);
        assertEquals("user123", data.get("username"));
        assertEquals("123456", data.get("password"));
    }

    @Test
    public void testLoginRequestEmailFlow() throws Exception {
        LoginRequest req = new LoginRequest(null, "user123@skygear.dev", "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:login", req.action);
        assertEquals("user123@skygear.dev", data.get("email"));
        assertEquals("123456", data.get("password"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailUsernameCoexistence() throws Exception {
        LoginRequest req = new LoginRequest("user123", "user123@skygear.dev", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailUsernameBothNull() throws Exception {
        LoginRequest req = new LoginRequest(null, null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateUsernameEmpty() throws Exception {
        LoginRequest req = new LoginRequest("", null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidateEmailEmpty() throws Exception {
        LoginRequest req = new LoginRequest(null, "", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLoginRequestInvalidatePasswordNull() throws Exception {
        LoginRequest req = new LoginRequest("user123", null, null);
        req.validate();
    }
}
