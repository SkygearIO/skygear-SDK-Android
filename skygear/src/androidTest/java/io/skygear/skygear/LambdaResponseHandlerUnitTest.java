package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class LambdaResponseHandlerUnitTest {
    @Test
    public void testLambdaResponseHandlerSuccessFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LambdaResponseHandler handler = new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                try {
                    assertEquals("world", result.getString("hello"));
                } catch (JSONException e) {
                    fail(e.getMessage());
                }

                checkpoints[0] = true;
            }

            @Override
            public void onLambdaFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(new JSONObject("{\"hello\":\"world\"}"));
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testLambdaResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = { false };
        LambdaResponseHandler handler = new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onLambdaFail(String reason) {
                assertEquals("Test Error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test Error"));
        assertTrue(checkpoints[0]);
    }
}