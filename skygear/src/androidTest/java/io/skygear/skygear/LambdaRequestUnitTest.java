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

@RunWith(AndroidJUnit4.class)
public class LambdaRequestUnitTest {
    @Test
    public void testLambdaRequestCreationFlow() throws Exception {
        LambdaRequest request = new LambdaRequest(
                "test:op1",
                new Object[]{"hello", "world", 123}
        );

        assertEquals("test:op1", request.action);

        Object[] args = (Object[]) request.data.get("args");
        assertEquals(3, args.length);
        assertEquals("hello", args[0]);
        assertEquals("world", args[1]);
        assertEquals(123, args[2]);

        request.validate();
    }

    @Test
    public void testLambdaRequestAllowNullArgumentList() throws Exception {
        LambdaRequest request = new LambdaRequest("test:op1", null);

        assertEquals("test:op1", request.action);
        assertNull(request.data.get("args"));
        assertFalse(request.data.keySet().contains("args"));

        request.validate();
    }

    @Test
    public void testLambdaRequestAllowNullArgument() throws Exception {
        LambdaRequest request = new LambdaRequest(
                "test:op1",
                new Object[]{"hello", "world", null}
        );

        assertEquals("test:op1", request.action);

        Object[] args = (Object[]) request.data.get("args");
        assertEquals(3, args.length);
        assertEquals("hello", args[0]);
        assertEquals("world", args[1]);
        assertNull(args[2]);

        request.validate();
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
        LambdaRequest request = new LambdaRequest(
                "test:op1",
                new Object[]{new Date()}
        );

        request.validate();
    }
}
