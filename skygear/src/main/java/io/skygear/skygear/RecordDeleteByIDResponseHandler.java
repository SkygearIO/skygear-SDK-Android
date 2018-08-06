package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Record Delete by ID Response Handler.
 */
public abstract class RecordDeleteByIDResponseHandler
        extends RecordDeleteResponseBaseHandler<String>
{
    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            if (results.length() != 1) {
                this.onDeleteFail(new Error("Malformed server response"));
                return;
            }

            Record aRecord = RecordSerializer.deserialize(results.getJSONObject(0));
            this.onDeleteSuccess(aRecord.getId());
        } catch (JSONException e) {
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }
}
