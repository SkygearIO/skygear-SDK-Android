package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Delete Multiple Records Response Handler.
 */
public abstract class MultiRecordDeleteResponseHandler
        extends RecordDeleteResponseBaseHandler<Record[]>
{
    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            Record[] records = new Record[results.length()];

            for (int idx = 0; idx < results.length(); idx++) {
                Record aRecord = RecordSerializer.deserialize(results.getJSONObject(idx));
                aRecord.deleted = true;

                records[idx] = aRecord;
            }

            this.onDeleteSuccess(records);
        } catch (JSONException e) {
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }
}
