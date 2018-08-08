package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordDeleteResponseHandlerUnitTest {
    @Test
    public void testRecordDeleteResponseNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_recordType", "Note");
        jsonObject.put("_recordID", "48092492-0791-4120-B314-022202AD3970");

        JSONArray results = new JSONArray();
        results.put(jsonObject);

        JSONObject resultObject = new JSONObject();
        resultObject.put("result", results);

        final boolean[] checkpoints = new boolean[] { false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(Record result) {
                assertEquals("Note", result.getType());
                assertEquals("48092492-0791-4120-B314-022202AD3970", result.getId());
                assertTrue(result.deleted);

                checkpoints[0] = true;
            }

            @Override
            public void onDeleteFail(Error error) {
                fail("Should not get error callback");
            }
        };

        handler.onSuccess(resultObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordDeleteResponseErrorFlow() throws JSONException {
        final boolean[] checkpoints = new boolean[] { false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(Record result) {
                fail("Should not call success callback");
            }

            @Override
            public void onDeleteFail(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}