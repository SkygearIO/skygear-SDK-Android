package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AuthResolveHandlerUnitTest {

    @Test
    @SmallTest
    public void testAuthResolveHandlerNormalFlow() throws Exception {
        AuthResolver authResolver = new AuthResolver() {
            @Override
            public void resolveAuthToken(String token) {
                assertEquals("my-token", token);
            }
        };

        AuthResolveHandler authResolveHandler = new AuthResolveHandler(authResolver);

        JSONObject data = new JSONObject();
        data.put("access_token", "my-token");

        authResolveHandler.onSuccess(data);
    }
}