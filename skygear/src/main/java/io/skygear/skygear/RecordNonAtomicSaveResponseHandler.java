package io.skygear.skygear;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Response Handler for Saving Record Non-Atomically
 */
public abstract class RecordNonAtomicSaveResponseHandler extends ResponseHandler {

    public abstract void onSaveSuccess(RecordResult<Record>[] results);

    public abstract void onSaveFail(Error error);

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray jsonResults = result.getJSONArray("result");
            RecordResult<Record>[] results = new RecordResult[jsonResults.length()];

            for (int idx = 0; idx < jsonResults.length(); idx++) {
                JSONObject eachJSONResult = jsonResults.getJSONObject(idx);
                String eachJSONResultType = eachJSONResult.getString("_type");

                switch (eachJSONResultType) {
                    case "record":
                        results[idx] = new RecordResult<>(
                                RecordSerializer.deserialize(eachJSONResult)
                        );
                        break;
                    case "error":
                        results[idx] = new RecordResult<>(
                                null,
                                ErrorSerializer.deserialize(eachJSONResult)
                        );
                        break;
                    default:
                        this.onSaveFail(new Error(
                                String.format("Unknown result type %s", eachJSONResultType)
                        ));
                        return;
                }
            }

            this.onSaveSuccess(results);
        } catch (JSONException e) {
            this.onSaveFail(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFail(Error error) {
        this.onSaveFail(error);
    }
}
