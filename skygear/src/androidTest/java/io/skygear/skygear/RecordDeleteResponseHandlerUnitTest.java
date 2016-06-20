package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class RecordDeleteResponseHandlerUnitTest {
    @Test
    public void testRecordDeleteResponseHandlerSuccessFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_id", "Note/48092492-0791-4120-B314-022202AD3970");
        jsonObject1.put("_type", "record");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject2.put("_type", "record");

        JSONArray result = new JSONArray();
        result.put(jsonObject1);
        result.put(jsonObject2);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                assertEquals(2, ids.length);
                assertEquals("48092492-0791-4120-B314-022202AD3970", ids[0]);
                assertEquals("48092492-0791-4120-B314-022202AD3971", ids[1]);
                checkpoints[0] = true;
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                fail("Should not get partial success callback");
            }

            @Override
            public void onDeleteFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordDeleteResponseHandlerPartialSuccessFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_id", "Note/48092492-0791-4120-B314-022202AD3970");
        jsonObject1.put("_type", "record");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject2.put("_type", "error");
        jsonObject2.put("code", 110);
        jsonObject2.put("message", "record not found");
        jsonObject2.put("name", "ResourceNotFound");

        JSONArray result = new JSONArray();
        result.put(jsonObject1);
        result.put(jsonObject2);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                fail("Should not get success callback");
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                assertEquals(1, ids.length);
                assertEquals(1, reasons.size());

                assertEquals("48092492-0791-4120-B314-022202AD3970", ids[0]);
                assertEquals("record not found", reasons.get("48092492-0791-4120-B314-022202AD3971"));

                checkpoints[0] = true;
            }

            @Override
            public void onDeleteFail(String reason) {
                fail("Should not get fail callback");
            }
        };

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordDeleteResponseHandlerAllFailFlow() throws Exception {
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("_id", "Note/48092492-0791-4120-B314-022202AD3970");
        jsonObject1.put("_type", "error");
        jsonObject1.put("code", 102);
        jsonObject1.put("message", "no permission to delete");
        jsonObject1.put("name", "PermissionDenied");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("_id", "Note/48092492-0791-4120-B314-022202AD3971");
        jsonObject2.put("_type", "error");
        jsonObject2.put("code", 110);
        jsonObject2.put("message", "record not found");
        jsonObject2.put("name", "ResourceNotFound");

        JSONArray result = new JSONArray();
        result.put(jsonObject1);
        result.put(jsonObject2);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result);

        final boolean[] checkpoints = new boolean[]{ false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                fail("Should not get success callback");
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                fail("Should not get partial success callback");
            }

            @Override
            public void onDeleteFail(String reason) {
                assertEquals("no permission to delete", reason);
                checkpoints[0] = true;
            }
        };

        handler.onSuccess(responseObject);
        assertTrue(checkpoints[0]);
    }

    @Test
    public void testRecordDeleteResponseHandlerFailFlow() throws Exception {
        final boolean[] checkpoints = new boolean[]{ false };
        RecordDeleteResponseHandler handler = new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                fail("Should not get success callback");
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                fail("Should not get partial success callback");
            }

            @Override
            public void onDeleteFail(String reason) {
                assertEquals("Unknown server error", reason);
                checkpoints[0] = true;
            }
        };

        handler.onFail(new Request.Error("Unknown server error"));
        assertTrue(checkpoints[0]);
    }
}
