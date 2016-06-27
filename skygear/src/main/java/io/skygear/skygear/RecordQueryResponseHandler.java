package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Record Query Response Handler.
 */
public abstract class RecordQueryResponseHandler implements Request.ResponseHandler {
    /**
     * Query success callback.
     *
     * @param records the records
     */
    public abstract void onQuerySuccess(Record[] records);

    /**
     * Query error callback.
     *
     * @param reason the reason
     */
    public abstract void onQueryError(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            Record[] records = new Record[results.length()];

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                records[idx] = Record.fromJson(perResult);
            }

            this.onQuerySuccess(records);
        } catch (JSONException e) {
            this.onQueryError("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onQueryError(error.getMessage());
    }
}
