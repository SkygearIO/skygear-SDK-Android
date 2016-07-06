package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class RequestUnitTest {
    static Map<String, Object> sampleData;
    static Request.ResponseHandler sampleHandler;

    @BeforeClass
    public static void setUpClass() throws Exception {
        RequestUnitTest.sampleData = new HashMap<>();
        RequestUnitTest.sampleData.put("hello", "world");
        RequestUnitTest.sampleData.put("foo", "bar");

        RequestUnitTest.sampleHandler = new Request.ResponseHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                // Do nothing
            }

            @Override
            public void onFail(Request.Error error) {
                // Do nothing
            }
        };
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        RequestUnitTest.sampleData = null;
        RequestUnitTest.sampleHandler = null;
    }

    @Test
    public void testRequestCreateNormalFlow() throws Exception {
        Request req = new Request(
                "test:action",
                RequestUnitTest.sampleData,
                RequestUnitTest.sampleHandler
        );

        assertEquals("test:action", req.action);
        assertEquals(RequestUnitTest.sampleData, req.data);
        assertEquals(RequestUnitTest.sampleHandler, req.responseHandler);
    }

    @Test
    public void testRequestCreateNoHandlerFlow() throws Exception {
        Request req = new Request(
                "test:action",
                RequestUnitTest.sampleData
        );

        assertEquals("test:action", req.action);
        assertEquals(RequestUnitTest.sampleData, req.data);
        assertNull(req.responseHandler);
    }
}
