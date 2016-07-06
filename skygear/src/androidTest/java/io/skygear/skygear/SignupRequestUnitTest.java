package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SignupRequestUnitTest {
    @Test
    public void testSignupRequestUsernameFlow() throws Exception {
        SignupRequest req = new SignupRequest("user123", null, "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:signup", req.action);
        assertEquals("user123", data.get("username"));
        assertEquals("123456", data.get("password"));
    }

    @Test
    public void testSignupRequestEmailFlow() throws Exception {
        SignupRequest req = new SignupRequest(null, "user123@skygear.dev", "123456");
        Map<String, Object> data = req.data;

        assertEquals("auth:signup", req.action);
        assertEquals("user123@skygear.dev", data.get("email"));
        assertEquals("123456", data.get("password"));
    }

    @Test(expected = InvalidParameterException.class)
    public void testSighupRequestInvalidateEmailUsernameCoexistence() throws Exception {
        SignupRequest req = new SignupRequest("user123", "user123@skygear.dev", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSighupRequestInvalidateEmailUsernameBothNull() throws Exception {
        SignupRequest req = new SignupRequest(null, null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSighupRequestInvalidateUsernameEmpty() throws Exception {
        SignupRequest req = new SignupRequest("", null, "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSighupRequestInvalidateEmailEmpty() throws Exception {
        SignupRequest req = new SignupRequest(null, "", "123456");
        req.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testSighupRequestInvalidatePasswordNull() throws Exception {
        SignupRequest req = new SignupRequest("user123", null, null);
        req.validate();
    }
}
