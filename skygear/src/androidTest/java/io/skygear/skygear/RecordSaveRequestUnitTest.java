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

import java.security.InvalidParameterException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecordSaveRequestUnitTest {
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
    public void testRecordSaveRequestNormalFlow() throws Exception {
        Record note1 = new Record("Note");
        Record note2 = new Record("Note");
        note1.set("identifier", 1);
        note2.set("identifier", 2);

        RecordSaveRequest recordSaveRequest
                = new RecordSaveRequest(new Record[]{note1, note2}, instrumentationPublicDatabase);
        Map<String, Object> data = recordSaveRequest.data;
        assertEquals("_public", data.get("database_id"));

        JSONArray records = (JSONArray) data.get("records");
        assertEquals(2, records.length());

        JSONObject record1 = (JSONObject) records.get(0);
        assertEquals(
                String.format("%s/%s", note1.getType(), note1.getId()),
                record1.getString("_id")
        );
        assertEquals(1, record1.getInt("identifier"));

        JSONObject record2 = (JSONObject) records.get(1);
        assertEquals(
                String.format("%s/%s", note2.getType(), note2.getId()),
                record2.getString("_id")
        );
        assertEquals(2, record2.getInt("identifier"));

        recordSaveRequest.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordSaveRequestNotAllowSaveNoRecords() throws Exception {
        RecordSaveRequest recordSaveRequest
                = new RecordSaveRequest(new Record[]{}, instrumentationPublicDatabase);
        recordSaveRequest.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordSaveRequestNotAllowMultiTypeRecords() throws Exception {
        RecordSaveRequest recordSaveRequest = new RecordSaveRequest(
                new Record[]{
                        new Record("Note"),
                        new Record("Comment")
                },
                instrumentationPublicDatabase
        );
        recordSaveRequest.validate();
    }

    @Test(expected = InvalidParameterException.class)
    public void testRecordSaveRequestNotAllowSavingRecordWithPendingAsset() throws Exception {
        Record note = new Record("Note");
        note.set("attachment", new Asset("hello.txt", "text/plain", "hello world".getBytes()));

        RecordSaveRequest recordSaveRequest = new RecordSaveRequest(
                new Record[]{ note },
                instrumentationPublicDatabase
        );
        recordSaveRequest.validate();
    }
}
