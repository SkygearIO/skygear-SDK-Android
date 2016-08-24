package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class GetCurrentUserRequestUnitTest {
    @Test
    public void testGetCurrentUserNormalFlow() throws Exception {
        GetCurrentUserRequest request = new GetCurrentUserRequest();

        assertEquals("me", request.action);
    }
}
