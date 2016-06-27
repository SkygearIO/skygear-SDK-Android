package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

/**
 * The Record Save Response Handler.
 */
public abstract class RecordSaveResponseHandler implements Request.ResponseHandler {

    /**
     * Save success callback.
     *
     * @param records the saved records
     */
    public abstract void onSaveSuccess(Record[] records);

    /**
     * partially save success callback.
     *
     * @param successRecords the successfully saved record map (recordId to record)
     * @param reasons        the fail reason map (recordId to reason String)
     */
    public abstract void onPartiallySaveSuccess(Map<String, Record> successRecords, Map<String, String> reasons);

    /**
     * Save fail callback.
     *
     * @param reason the reason
     */
    public abstract void onSaveFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            Map<String, Record> recordMap = new TreeMap<>();
            Map<String, String> errorMap = new TreeMap<>();

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultId = perResult.getString("_id").split("/", 2)[1];
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record":
                        recordMap.put(perResultId, Record.fromJson(perResult));
                        break;
                    case "error":
                        errorMap.put(perResultId, perResult.getString("message"));
                        break;
                    default:
                        this.onSaveFail(String.format(
                                "Malformed server response - Unknown result type \"%s\"",
                                perResultType
                        ));
                        return;
                }
            }

            if (errorMap.size() == 0) {
                // all success
                Record[] records = new Record[recordMap.size()];
                recordMap.values().toArray(records);

                this.onSaveSuccess(records);
            } else if (recordMap.size() == 0) {
                // all fail
                this.onSaveFail(errorMap.values().iterator().next());
            } else {
                // partial success
                this.onPartiallySaveSuccess(recordMap, errorMap);
            }
        } catch (JSONException e) {
            this.onSaveFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onSaveFail(error.getMessage());
    }
}
