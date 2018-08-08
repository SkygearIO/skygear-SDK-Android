package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordSaveResponseHandlerUnitTest {

    @Test
    public void testRecordSaveResponseHandlerNormalFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_recordType", "Note");
        jsonObject1.put("_recordID", "48092492-0791-4120-B314-022202AD3970");
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

        RecordSaveResponseHandler handler = new RecordSaveResponseHandler() {
            @Override
            public void onSaveSuccess(Record result) {
                assertEquals("Note", result.getType());
                assertEquals("48092492-0791-4120-B314-022202AD3970", result.getId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 32, 342, DateTimeZone.UTC).toDate(),
                        result.getCreatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc0", result.getCreatorId());
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc1", result.getOwnerId());
                assertEquals(
                        new DateTime(2016, 6, 15, 7, 55, 33, 342, DateTimeZone.UTC).toDate(),
                        result.getUpdatedAt()
                );
                assertEquals("5a497b0b-cf93-4720-bea4-14637478cfc2", result.getUpdaterId());
                assertEquals("world1", result.get("hello"));
                assertEquals(3, result.get("foobar"));
                assertEquals(12.345, result.get("abc"));

                checkpoints[0] = true;
            }

            @Override
            public void onSaveFail(Error error) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordSaveResponseHandlerErrorFlow() {
        final boolean[] checkpoints = new boolean[]{ false };
        RecordSaveResponseHandler handler = new RecordSaveResponseHandler() {
            @Override
            public void onSaveSuccess(Record result) {
                fail("Should not get success callback");
            }

            @Override
            public void onSaveFail(Error error) {
                assertEquals("Unknown server error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("Unknown server error"));
        assertTrue(checkpoints[0]);
    }
}
