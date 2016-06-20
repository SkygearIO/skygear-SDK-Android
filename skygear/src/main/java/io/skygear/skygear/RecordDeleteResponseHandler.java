package io.skygear.skygear;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Record Delete Response Handler.
 */
public abstract class RecordDeleteResponseHandler implements Request.ResponseHandler {

    /**
     * Delete success callback.
     *
     * @param ids the deleted record ids
     */
    public abstract void onDeleteSuccess(String[] ids);

    /**
     * Partially delete success callback.
     *
     * @param ids     the deleted record ids
     * @param reasons the fail reason map (recordId to reason String)
     */
    public abstract void onDeletePartialSuccess(String[] ids, Map<String, String> reasons);

    /**
     * Delete fail callback.
     *
     * @param reason the reason
     */
    public abstract void onDeleteFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            List<String> successList = new LinkedList<>();
            Map<String, String> errorMap = new TreeMap<>();

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultId = perResult.getString("_id").split("/", 2)[1];
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record":
                        successList.add(perResultId);
                        break;
                    case "error":
                        errorMap.put(perResultId, perResult.getString("message"));
                        break;
                    default:
                        this.onDeleteFail(String.format(
                                "Malformed server response - Unknown result type \"%s\"",
                                perResultType
                        ));
                        return;
                }
            }

            if (errorMap.size() == 0) {
                // all success
                this.onDeleteSuccess(successList.toArray(new String[]{}));
            } else if (successList.size() == 0) {
                // all fail
                this.onDeleteFail(errorMap.values().iterator().next());
            } else {
                // partial success
                this.onDeletePartialSuccess(successList.toArray(new String[]{}), errorMap);
            }
        } catch (JSONException e) {
            this.onDeleteFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onDeleteFail(error.getMessage());
    }
}
