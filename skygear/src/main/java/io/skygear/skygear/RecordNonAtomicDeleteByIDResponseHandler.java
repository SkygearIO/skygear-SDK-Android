package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Response Handler for Non-Atomic Deleting Record by ID
 */
public abstract class RecordNonAtomicDeleteByIDResponseHandler
        extends RecordNonAtomicDeleteResponseBaseHandler<String>
{
    @Override
    protected final String parseRecordJSONObject(JSONObject jsonObject) throws JSONException {
        return RecordSerializer.deserialize(jsonObject).getId();
    }
}
