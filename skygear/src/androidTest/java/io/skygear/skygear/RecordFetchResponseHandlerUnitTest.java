package io.skygear.skygear;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordFetchResponseHandlerUnitTest {
    static Context instrumentationContext;
    static Container instrumentationContainer;
    static Database instrumentationPublicDatabase;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        instrumentationContainer = new Container(instrumentationContext, Configuration.testConfiguration());
        instrumentationPublicDatabase = Database.Factory.publicDatabase(instrumentationContainer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
        instrumentationPublicDatabase = null;
    }

    @Test
    public void testRecordFetchResponseHandlerNormalFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_id", "Note/48092492-0791-4120-B314-022202AD3970");
        jsonObject1.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject1.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject1.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject1.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject1.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject1.put("_type", "record");
        jsonObject1.put("_access", null);
        jsonObject1.put("hello", "world1");
        jsonObject1.put("foobar", 3);
        jsonObject1.put("abc", 12.345);

        JSONArray result = new JSONArray();
        result.put(jsonObject1);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordFetchRequest request = new RecordFetchRequest(
                "Note",
                "48092492-0791-4120-B314-022202AD3970",
                instrumentationPublicDatabase
        );
        request.setResponseHandler(new RecordFetchResponseHandler() {
            @Override
            public void onFetchSuccess(Record result) {
                assertEquals("world1", result.get("hello"));
                assertEquals(3, result.get("foobar"));
                assertEquals(12.345, result.get("abc"));

                checkpoints[0] = true;
            }

            @Override
            public void onFetchError(Error error) {
                fail("Should not get error callback");
            }
        });

        request.getResponseHandler().onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testMultiRecordFetchResponseHandlerMissingFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_id", "Note/48092492-0791-4120-B314-022202AD3970");
        jsonObject1.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject1.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject1.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject1.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject1.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject1.put("_type", "record");
        jsonObject1.put("_access", null);
        jsonObject1.put("hello", "world1");
        jsonObject1.put("foobar", 3);
        jsonObject1.put("abc", 12.345);

        JSONArray result = new JSONArray();
        result.put(jsonObject1);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordFetchRequest request = new RecordFetchRequest(
                "Note",
                "missing-id",
                instrumentationPublicDatabase
        );
        request.setResponseHandler(new RecordFetchResponseHandler() {
            @Override
            public void onFetchSuccess(Record result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFetchError(Error error) {
                assertEquals(Error.Code.RESOURCE_NOT_FOUND, error.getCode());
                assertEquals(
                        "Cannot find Note record with ID missing-id",
                        error.getDetailMessage()
                );
                checkpoints[0] = true;
            }
        });

        request.getResponseHandler().onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testMultiRecordFetchResponseHandlerOperationErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        RecordFetchRequest request = new RecordFetchRequest(
                "Note",
                new String[]{
                        "48092492-0791-4120-B314-022202AD3970",
                        "48092492-0791-4120-B314-022202AD3971"
                },
                instrumentationPublicDatabase
        );
        request.setResponseHandler(new RecordFetchResponseHandler() {
            @Override
            public void onFetchSuccess(Record result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFetchError(Error error) {
                assertEquals("Some Error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        });

        request.getResponseHandler().onFailure(new Error("Some Error"));
        assertTrue(checkpoints[0]);
    }
}
