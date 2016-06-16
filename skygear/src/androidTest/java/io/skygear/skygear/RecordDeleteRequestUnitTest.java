package io.skygear.skygear;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.InvalidParameterException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecordDeleteRequestUnitTest {
    static Context instrumentationContext;
    static Container instrumentationContainer;
    static Database instrumentationPublicDatabase;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        instrumentationContainer = new Container(instrumentationContext, Configuration.defaultConfiguration());
        instrumentationPublicDatabase= Database.publicDatabase(instrumentationContainer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
        instrumentationPublicDatabase = null;
    }

    @Test
    public void testRecordDeleteRequestNormalFlow() throws Exception {
        Record note1 = new Record("Note");
        Record note2 = new Record("Note");

        note1.id = "9C0F4536-FEA7-42DB-B8EF-561CCD175E06";
        note2.id = "05A2946A-72DC-4F20-99F9-129BD1FCB52A";

        RecordDeleteRequest request
                = new RecordDeleteRequest(new Record[]{note1, note2}, instrumentationPublicDatabase);
        Map<String, Object> data = request.data;
        assertEquals("_public", data.get("database_id"));

        JSONArray ids = (JSONArray) data.get("ids");
        assertEquals(2, ids.length());

        assertEquals(
                String.format("%s/%s", note1.getType(), note1.getId()),
                ids.get(0)
        );
        assertEquals(
                String.format("%s/%s", note2.getType(), note2.getId()),
                ids.get(1)
        );

        request.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordSaveRequestNotAllowSaveNoRecords() throws Exception {
        RecordDeleteRequest request
                = new RecordDeleteRequest(new Record[]{}, instrumentationPublicDatabase);
        request.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordSaveRequestNotAllowMultiTypeRecords() throws Exception {
        RecordDeleteRequest request = new RecordDeleteRequest(
                new Record[]{
                        new Record("Note"),
                        new Record("Comment")
                },
                instrumentationPublicDatabase
        );
        request.validate();
    }
}
