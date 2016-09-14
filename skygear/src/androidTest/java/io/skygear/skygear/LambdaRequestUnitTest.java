package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LambdaRequestUnitTest {
    @Test
    public void testLambdaRequestCreationFlow() throws Exception {
        LambdaRequest request = new LambdaRequest(
                "test:op1",
                new Object[]{"hello", "world", 123}
        );

        assertEquals("test:op1", request.action);

        JSONArray args = (JSONArray) request.data.get("args");
        assertEquals(3, args.length());
        assertEquals("hello", args.get(0));
        assertEquals("world", args.get(1));
        assertEquals(123, args.get(2));
    }

    @Test
    public void testLambdaRequestAllowNullArgumentList() throws Exception {
        LambdaRequest request = new LambdaRequest("test:op1", null);

        assertEquals("test:op1", request.action);
        assertNull(request.data.get("args"));
        assertFalse(request.data.keySet().contains("args"));
    }

    @Test
    public void testLambdaRequestAllowNullArgument() throws Exception {
        LambdaRequest request = new LambdaRequest(
                "test:op1",
                new Object[]{"hello", "world", null}
        );

        assertEquals("test:op1", request.action);

        JSONArray args = (JSONArray) request.data.get("args");
        assertEquals(3, args.length());
        assertEquals("hello", args.get(0));
        assertEquals("world", args.get(1));
        assertTrue(args.isNull(2));
    }

    @Test
    public void testLambdaRequestCompatibleValueValidation() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"hello\":\"world\"}");
        JSONArray jsonArray = new JSONArray("[\"hello\",\"world\"]");

        LambdaRequest request = new LambdaRequest("test:op1", new Object[]{
                false,
                (byte) 3,
                'c',
                3.4,
                3.4f,
                3,
                3L,
                (short) 3,
                "3",
                jsonObject,
                jsonArray
        });

        request.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testLambdaRequestIncompatibleValueValidation() throws Exception {
        new LambdaRequest("test:op1", new Object[]{new Date()});
    }
}
