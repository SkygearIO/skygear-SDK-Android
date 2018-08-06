package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MultiRecordDeleteResponseHandlerUnitTest {
    @Test
    public void testMultiRecordDeleteResponseNormalFlow() throws Exception {
        JSONArray results = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_recordType", "Note");
        jsonObject1.put("_recordID", "48092492-0791-4120-B314-022202AD3970");
        results.put(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_recordType", "Note");
        jsonObject2.put("_recordID", "48092492-0791-4120-B314-022202AD3971");
        results.put(jsonObject2);


        JSONObject resultObject = new JSONObject();
        resultObject.put("result", results);

        final boolean[] checkpoints = new boolean[] { false };
        MultiRecordDeleteResponseHandler handler = new MultiRecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(Record[] result) {
                assertEquals("Note", result[0].getType());
                assertEquals("48092492-0791-4120-B314-022202AD3970", result[0].getId());
                assertTrue(result[0].deleted);

                assertEquals("Note", result[1].getType());
                assertEquals("48092492-0791-4120-B314-022202AD3971", result[1].getId());
                assertTrue(result[1].deleted);

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
    public void testMultiRecordDeleteResponseErrorFlow() throws JSONException {
        final boolean[] checkpoints = new boolean[] { false };
        MultiRecordDeleteResponseHandler handler = new MultiRecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(Record[] result) {
                fail("Should not call success callback");
            }

            @Override
            public void onDeleteFail(Error error) {
                assertEquals("Test error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Error("Test error"));
        assertTrue(checkpoints[0]);
    }
}