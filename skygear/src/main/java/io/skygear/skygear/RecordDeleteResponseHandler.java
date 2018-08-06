/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
public abstract class RecordDeleteResponseHandler extends ResponseHandler {

    /**
     * Delete success callback.
     *
     * @param ids the deleted record ids
     */
    public abstract void onDeleteSuccess(String[] ids);

    /**
     * Partially delete success callback.
     *
     * @param ids    the deleted record ids (null when fail to delete the corresponding record)
     * @param errors the errors (null when the corresponding record is deleted successfully)
     */
    public abstract void onDeletePartialSuccess(String[] ids, Error[] errors);

    /**
     * Delete fail callback.
     *
     * @param error the error
     */
    public abstract void onDeleteFail(Error error);

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            List<String> successList = new LinkedList<>();
            List<Error> errorList = new LinkedList<>();

            boolean hasSuccess = false;
            boolean hasError = false;

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultId = perResult.getString("_recordID");
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record": {
                        hasSuccess = true;
                        successList.add(perResultId);
                        errorList.add(null);
                        break;
                    }
                    case "error": {
                        hasError = true;
                        successList.add(null);
                        errorList.add(ErrorSerializer.deserialize(perResult));
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

            if (hasSuccess && hasError) {
                // partial success
                this.onDeletePartialSuccess(
                        successList.toArray(new String[]{}),
                        errorList.toArray(new Error[]{})
                );
            } else if (hasError) {
                // all fail
                this.onFail(errorList.get(0));
            } else {
                // all success
                this.onDeleteSuccess(successList.toArray(new String[]{}));
            }
        } catch (JSONException e) {
            this.onDeleteFail(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFail(Error error) {
        this.onDeleteFail(error);
    }
}
