package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordNonAtomicDeleteByIDResponseHandlerUnitTest {
    @Test
    public void testRecordNonAtomicDeleteByIDResponseSomeErrorsFlow() throws Exception {
        JSONArray results = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_type", "record");
        jsonObject1.put("_recordType", "Note");
        jsonObject1.put("_recordID", "48092492-0791-4120-B314-022202AD3970");
        results.put(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_type", "error");
        jsonObject2.put("code", 110);
        jsonObject2.put("message", "record not found");
        jsonObject2.put("type", "ResourceNotFound");
        results.put(jsonObject2);

        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("_type", "record");
        jsonObject3.put("_recordType", "Note");
        jsonObject3.put("_recordID", "48092492-0791-4120-B314-022202AD3971");
        results.put(jsonObject3);

        JSONObject resultObject = new JSONObject();
        resultObject.put("result", results);

        final boolean[] checkpoints = new boolean[] { false };
        RecordNonAtomicDeleteByIDResponseHandler handler = new RecordNonAtomicDeleteByIDResponseHandler() {
            @Override
            public void onDeleteSuccess(RecordResult<String>[] results) {
                assertEquals(3, results.length);
                assertEquals("48092492-0791-4120-B314-022202AD3970", results[0].value);
                assertEquals("48092492-0791-4120-B314-022202AD3971", results[2].value);

                assertNull(results[1].value);
                assertTrue(results[1].isError());
                assertEquals(110, results[1].error.getCode().getValue());
                assertEquals("record not found", results[1].error.getDetailMessage());

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
    public void testRecordNonAtomicDeleteByIDResponseNormalFlow() throws Exception {
        JSONArray results = new JSONArray();

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_type", "record");
        jsonObject1.put("_recordType", "Note");
        jsonObject1.put("_recordID", "48092492-0791-4120-B314-022202AD3970");
        results.put(jsonObject1);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_type", "record");
        jsonObject2.put("_recordType", "Note");
        jsonObject2.put("_recordID", "48092492-0791-4120-B314-022202AD3971");
        results.put(jsonObject2);

        JSONObject resultObject = new JSONObject();
        resultObject.put("result", results);

        final boolean[] checkpoints = new boolean[] { false };
        RecordNonAtomicDeleteByIDResponseHandler handler = new RecordNonAtomicDeleteByIDResponseHandler() {
            @Override
            public void onDeleteSuccess(RecordResult<String>[] results) {
                assertEquals(2, results.length);
                assertEquals("48092492-0791-4120-B314-022202AD3970", results[0].value);
                assertEquals("48092492-0791-4120-B314-022202AD3971", results[1].value);

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
    public void testRecordNonAtomicDeleteByIDResponseOperationalErrorFlow() throws Exception {
        final boolean[] checkpoints = new boolean[] { false };
        RecordNonAtomicDeleteByIDResponseHandler handler = new RecordNonAtomicDeleteByIDResponseHandler() {
            @Override
            public void onDeleteSuccess(RecordResult<String>[] results) {
                fail("Should not get success callback");
            }

            @Override
            public void onDeleteFail(Error error) {
                assertEquals("some-error", error.getDetailMessage());
                checkpoints[0] = true;
            }
        };

        handler.onFailure(new Error("some-error"));
        assertTrue(checkpoints[0]);
    }
}