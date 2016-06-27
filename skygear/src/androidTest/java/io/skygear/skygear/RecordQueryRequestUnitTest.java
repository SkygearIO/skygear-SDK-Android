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
public class RecordQueryRequestUnitTest {
    static Context instrumentationContext;
    static Container instrumentationContainer;
    static Database instrumentationPublicDatabase;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        instrumentationContainer = new Container(instrumentationContext, Configuration.defaultConfiguration());
        instrumentationPublicDatabase = Database.publicDatabase(instrumentationContainer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
        instrumentationPublicDatabase = null;
    }

    @Test
    public void testRecordQueryRequestNormalFlow() throws Exception {
        Query query = new Query("Note")
                .equalTo("title", "Hello World")
                .addDescending("rating");

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        Map<String, Object> data = request.data;

        assertEquals("_public", data.get("database_id"));
        assertEquals("Note", data.get("record_type"));

        JSONArray predicate = (JSONArray) data.get("predicate");
        assertEquals("eq", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello World", predicate.getString(2));

        JSONArray sortPredicate = (JSONArray) data.get("sort");
        JSONArray sortPredicate1 = sortPredicate.getJSONArray(0);

        JSONObject sortPredicate1Keypath = sortPredicate1.getJSONObject(0);
        assertEquals("keypath", sortPredicate1Keypath.getString("$type"));
        assertEquals("rating", sortPredicate1Keypath.getString("$val"));

        assertEquals("desc", sortPredicate1.getString(1));
    }
}
