package io.skygear.skygear;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base Abstract Class for The Record Save Response Handler
 * @param <T> The Result Type
 */
public abstract class RecordSaveResponseBaseHandler<T> extends ResponseHandler {

    /**
     * Save success callback.
     *
     * @param result the save result
     */
    public abstract void onSaveSuccess(T result);

    /**
     * Save fail callback.
     *
     * @param error the error
     */
    public abstract void onSaveFail(Error error);

    protected abstract void onSaveRecordsSuccess(Record[] records);

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray jsonResults = result.getJSONArray("result");
            Record[] records = new Record[jsonResults.length()];
            for (int idx = 0; idx < jsonResults.length(); idx++) {
                JSONObject eachJSONResult = jsonResults.getJSONObject(idx);
                records[idx] = RecordSerializer.deserialize(eachJSONResult);
            }

            this.onSaveRecordsSuccess(records);
        } catch (JSONException e) {
            this.onSaveFail(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFailure(Error error) {
        this.onSaveFail(error);
    }
}
