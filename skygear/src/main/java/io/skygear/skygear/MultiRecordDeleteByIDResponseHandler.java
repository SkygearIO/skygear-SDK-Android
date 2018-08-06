package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Delete Multiple Record by ID Response Handler.
 */
public abstract class MultiRecordDeleteByIDResponseHandler
        extends RecordDeleteResponseBaseHandler<String[]>
{
    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            String[] recordIDs = new String[results.length()];

            for (int idx = 0; idx < results.length(); idx++) {
                Record aRecord = RecordSerializer.deserialize(results.getJSONObject(idx));
                recordIDs[idx] = aRecord.getId();
            }

            this.onDeleteSuccess(recordIDs);
        } catch (JSONException e) {
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }
}
