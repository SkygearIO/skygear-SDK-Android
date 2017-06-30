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

import java.util.Map;
import java.util.TreeMap;

/**
 * The Record Save Response Handler.
 */
public abstract class RecordSaveResponseHandler implements ResponseHandler {

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
     * @param errors         the errors (recordId to error)
     */
    public abstract void onPartiallySaveSuccess(Map<String, Record> successRecords, Map<String, Error> errors);

    /**
     * Save fail callback.
     *
     * @param error the error
     */
    public abstract void onSaveFail(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            Map<String, Record> recordMap = new TreeMap<>();
            Map<String, Error> errorMap = new TreeMap<>();

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultId = perResult.getString("_id").split("/", 2)[1];
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record":
                        recordMap.put(perResultId, Record.fromJson(perResult));
                        break;
                    case "error":{
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
                        this.onSaveFail(new Error(errorMessage));
                        return;
                    }
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
            this.onSaveFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onSaveFail(error);
    }
}
