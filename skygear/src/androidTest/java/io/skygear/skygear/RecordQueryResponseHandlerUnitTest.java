package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class RecordQueryResponseHandlerUnitTest {

    @Test
    public void testRecordQueryResponseHandlerSuccessFlow() throws Exception {
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

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject2.put("_created_at", "2016-06-15T07:55:32.342Z");
        jsonObject2.put("_created_by", "5a497b0b-cf93-4720-bea4-14637478cfc0");
        jsonObject2.put("_ownerID", "5a497b0b-cf93-4720-bea4-14637478cfc1");
        jsonObject2.put("_updated_at", "2016-06-15T07:55:33.342Z");
        jsonObject2.put("_updated_by", "5a497b0b-cf93-4720-bea4-14637478cfc2");
        jsonObject2.put("_type", "record");
        jsonObject2.put("_access", null);
        jsonObject2.put("hello", "world2");
        jsonObject2.put("foobar", 4);
        jsonObject2.put("abc", 22.345);

        JSONArray result = new JSONArray();
        result.put(jsonObject1);
        result.put(jsonObject2);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordQueryResponseHandler handler = new RecordQueryResponseHandler() {
            @Override
            public void onQuerySuccess(Record[] records) {
                assertEquals(2, records.length);

                Record record1 = records[0];
                assertEquals("Note", record1.getType());
                assertEquals("48092492-0791-4120-B314-022202AD3970", record1.getId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 32, 342, DateTimeZone.UTC).toDate(),
                        record1.getCreatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc0", record1.getCreatorId());
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc1", record1.getOwnerId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 33, 342, DateTimeZone.UTC).toDate(),
                        record1.getUpdatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc2", record1.getUpdaterId());
                assertEquals("world1", record1.get("hello"));
                assertEquals(3, record1.get("foobar"));
                assertEquals(12.345, record1.get("abc"));

                Record record2 = records[1];
                assertEquals("Note", record2.getType());
                assertEquals("48092492-0791-4120-B314-022202AD3971", record2.getId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 32, 342, DateTimeZone.UTC).toDate(),
                        record2.getCreatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc0", record2.getCreatorId());
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc1", record2.getOwnerId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 33, 342, DateTimeZone.UTC).toDate(),
                        record2.getUpdatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc2", record2.getUpdaterId());
                assertEquals("world2", record2.get("hello"));
                assertEquals(4, record2.get("foobar"));
                assertEquals(22.345, record2.get("abc"));

                checkpoints[0] = true;
            }

            @Override
            public void onQueryError(String reason) {
                fail("Should not get error callback");
            }
        };

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordQueryResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        RecordQueryResponseHandler handler = new RecordQueryResponseHandler() {
            @Override
            public void onQuerySuccess(Record[] records) {
                fail("Should not get success callback");
            }

            @Override
            public void onQueryError(String reason) {
                assertEquals("Test error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}
