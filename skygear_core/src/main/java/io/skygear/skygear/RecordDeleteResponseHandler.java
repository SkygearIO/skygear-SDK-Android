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
public abstract class RecordDeleteResponseHandler implements ResponseHandler {

    /**
     * Delete success callback.
     *
     * @param ids the deleted record ids
     */
    public abstract void onDeleteSuccess(String[] ids);

    /**
     * Partially delete success callback.
     *
     * @param ids    the deleted record ids
     * @param errors the errors (recordId to error)
     */
    public abstract void onDeletePartialSuccess(String[] ids, Map<String, Error> errors);

    /**
     * Delete fail callback.
     *
     * @param error the error
     */
    public abstract void onDeleteFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            List<String> successList = new LinkedList<>();
            Map<String, Error> errorMap = new TreeMap<>();

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultId = perResult.getString("_id").split("/", 2)[1];
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record":
                        successList.add(perResultId);
                        break;
                    case "error": {
                        int errorCodeValue = perResult.optInt("code", 0);
                        String errorMessage = perResult.getString("message");
                        errorMap.put(perResultId, new Error(errorCodeValue, errorMessage));
                        break;
                    }
                    default: {
                        String errorMessage = String.format(
                                "Malformed server response - Unknown result type \"%s\"",
                                perResultType
                        );
                        this.onDeleteFail(new Error(errorMessage));
                        return;
                    }
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
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onDeleteFail(error);
    }
}
