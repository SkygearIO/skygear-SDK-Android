package io.skygear.skygear;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

        Assert.assertEquals("test:action", req.action);
        Assert.assertEquals(RequestUnitTest.sampleData, req.data);
        Assert.assertEquals(RequestUnitTest.sampleHandler, req.responseHandler);
    }

    @Test
    public void testRequestCreateNoHandlerFlow() throws Exception {
        Request req = new Request(
                "test:action",
                RequestUnitTest.sampleData
        );

        Assert.assertEquals("test:action", req.action);
        Assert.assertEquals(RequestUnitTest.sampleData, req.data);
        Assert.assertNull(req.responseHandler);
    }
}
