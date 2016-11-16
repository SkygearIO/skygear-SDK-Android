package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Record Query Response Handler.
 */
public abstract class RecordQueryResponseHandler implements ResponseHandler {
    /**
     * Query success callback.
     *
     * @param records the records
     */
    public abstract void onQuerySuccess(Record[] records);

    /**
     * Query error callback.
     *
     * @param error the error
     */
    public abstract void onQueryError(Error error);

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
            this.onQueryError(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onQueryError(error);
    }
}
