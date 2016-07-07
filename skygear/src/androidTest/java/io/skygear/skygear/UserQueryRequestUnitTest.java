package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserQueryRequestUnitTest {
    @Test
    public void testUserQueryRequestCreationFlow() throws Exception {
        UserQueryRequest request = new UserQueryRequest(new String[] {
                "hello@skygear.dev",
                "world@skygear.dev",
                "foo@skygear.dev",
                "bar@skygear.dev"
        });

        assertEquals("user:query", request.action);

        String[] emails = (String[]) request.data.get("emails");
        assertEquals(4, emails.length);
        assertEquals("hello@skygear.dev", emails[0]);
        assertEquals("world@skygear.dev", emails[1]);
        assertEquals("foo@skygear.dev", emails[2]);
        assertEquals("bar@skygear.dev", emails[3]);
    }

    @Test(expected = InvalidParameterException.class)
    public void testUserQueryRequestCreationFlowWithNoEmails() throws Exception {
        UserQueryRequest request = new UserQueryRequest(new String[] {});
        request.validate();
    }
}
