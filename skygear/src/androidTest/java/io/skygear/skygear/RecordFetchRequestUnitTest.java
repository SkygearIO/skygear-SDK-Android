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

import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecordFetchRequestUnitTest {
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
    public void testRecordFetchRequestNormalFlow() throws Exception {
        RecordFetchRequest request = new RecordFetchRequest(
                "Note",
                new String[]{"1", "2"},
                instrumentationPublicDatabase
        );

        String[] fetchingRecordIDs = request.fetchingRecordIDs;
        assertEquals(2, fetchingRecordIDs.length);
        assertEquals("1", fetchingRecordIDs[0]);
        assertEquals("2", fetchingRecordIDs[1]);

        Map<String, Object> data = request.data;

        assertEquals("_public", data.get("database_id"));
        assertEquals("Note", data.get("record_type"));

        JSONArray predicate = (JSONArray) data.get("predicate");
        assertEquals("in", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("_id", predicateKeypath.getString("$val"));

        JSONArray predicateLookups = predicate.getJSONArray(2);

        assertEquals("1", predicateLookups.getString(0));
        assertEquals("2", predicateLookups.getString(1));
    }

    @Test
    public void testRecordFetchRequestSingleIDFlow() throws Exception {
        RecordFetchRequest request = new RecordFetchRequest(
                "Note",
                "123",
                instrumentationPublicDatabase
        );

        String[] fetchingRecordIDs = request.fetchingRecordIDs;
        assertEquals(1, fetchingRecordIDs.length);
        assertEquals("123", fetchingRecordIDs[0]);

        Map<String, Object> data = request.data;

        assertEquals("_public", data.get("database_id"));
        assertEquals("Note", data.get("record_type"));

        JSONArray predicate = (JSONArray) data.get("predicate");
        assertEquals("in", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("_id", predicateKeypath.getString("$val"));

        JSONArray predicateLookups = predicate.getJSONArray(2);

        assertEquals("123", predicateLookups.getString(0));
    }
}