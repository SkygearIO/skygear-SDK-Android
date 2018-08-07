package io.skygear.skygear;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Response Handler for Non-Atomic Deleting Record
 */
public abstract class RecordNonAtomicDeleteResponseHandler
        extends RecordNonAtomicDeleteResponseBaseHandler<Record>
{
    @Override
    protected final Record parseRecordJSONObject(JSONObject jsonObject) throws JSONException {
        Record aRecord = RecordSerializer.deserialize(jsonObject);
        aRecord.deleted = true;

        return aRecord;
    }
}
