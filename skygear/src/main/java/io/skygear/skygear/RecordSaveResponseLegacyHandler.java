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

/**
 * The Record Save Response Handler.
 */
public abstract class RecordSaveResponseLegacyHandler extends ResponseHandler {

    /**
     * Save success callback.
     *
     * @param records the saved records
     */
    public abstract void onSaveSuccess(Record[] records);

    /**
     * partially save success callback.
     *
     * @param successRecords the successfully saved record
     *                       (null when fail to save the corresponding record)
     * @param errors         the errors (null when the corresponding record is saved successfully)
     */
    public abstract void onPartiallySaveSuccess(Record[] successRecords, Error[] errors);

    /**
     * Save fail callback.
     *
     * @param error the error
     */
    public abstract void onSaveFail(Error error);

    @Override
    public final void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");

            int resultSize = results.length();

            List<Record> recordList = new LinkedList<>();
            List<Error> errorList = new LinkedList<>();

            boolean hasSuccess = false;
            boolean hasError = false;

            for (int idx = 0; idx < resultSize; idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                String perResultType = perResult.getString("_type");

                switch (perResultType) {
                    case "record": {
                        hasSuccess = true;
                        recordList.add(Record.fromJson(perResult));
                        errorList.add(null);
                        break;
                    }
                    case "error":{
                        hasError = true;
                        recordList.add(null);
                        errorList.add(ErrorSerializer.deserialize(perResult));
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

            if (hasSuccess && hasError) {
                // partial success
                this.onPartiallySaveSuccess(
                        recordList.toArray(new Record[resultSize]),
                        errorList.toArray(new Error[resultSize])
                );
            } else if (hasError) {
                // all fail
                this.onSaveFail(errorList.get(0));
            } else {
                // all success
                this.onSaveSuccess(recordList.toArray(new Record[resultSize]));
            }
        } catch (JSONException e) {
            this.onSaveFail(new Error("Malformed server response"));
        }
    }

    @Override
    public final void onFail(Error error) {
        this.onSaveFail(error);
    }
}
