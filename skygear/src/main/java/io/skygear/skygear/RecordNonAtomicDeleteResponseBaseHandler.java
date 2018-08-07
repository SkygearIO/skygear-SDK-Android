package io.skygear.skygear;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Base Class for Non-Atomic Delete Record Response Handler.
 *
 * @param <T> the type parameter
 */
public abstract class RecordNonAtomicDeleteResponseBaseHandler<T> extends ResponseHandler {

    /**
     * Delete success callback.
     *
     * @param results the results
     */
    public abstract void onDeleteSuccess(RecordResult<T>[] results);

    /**
     * Delete operational error callback
     *
     * @param error the error
     */
    public abstract void onDeleteFail(Error error);

    protected abstract T parseRecordJSONObject(JSONObject jsonObject) throws JSONException;

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray jsonResults = result.getJSONArray("result");
            RecordResult<T>[] results = new RecordResult[jsonResults.length()];

            for (int idx = 0; idx < jsonResults.length(); idx++) {
                JSONObject eachJSONResult = jsonResults.getJSONObject(idx);
                String eachJSONResultType = eachJSONResult.getString("_type");

                switch (eachJSONResultType) {
                    case "record":
                        results[idx] = new RecordResult<>(
                                this.parseRecordJSONObject(eachJSONResult)
                        );
                        break;
                    case "error":
                        results[idx] = new RecordResult<>(
                                null,
                                ErrorSerializer.deserialize(eachJSONResult)
                        );
                        break;
                    default:
                        this.onDeleteFail(new Error(
                                String.format("Unknown result type %s", eachJSONResultType)
                        ));
                        return;
                }
            }

            this.onDeleteSuccess(results);
        } catch (JSONException e) {
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFail(Error error) {
        this.onDeleteFail(error);
    }
}
