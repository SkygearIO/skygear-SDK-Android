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

/**
 * The Skygear Record Query Response Handler.
 */
public abstract class RecordQueryResponseHandler implements ResponseHandler {
    /**
     * Query success callback.
     *
     * @param records the records
     */
    public void onQuerySuccess(Record[] records) {}

    /**
     * Query success callback.
     *
     * @param records the records
     */
    public void onQuerySuccess(Record[] records, QueryInfo queryInfo) {}

    /**
     * Query error callback.
     *
     * @param error the error
     */
    public abstract void onQueryError(Error error);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            JSONArray results = result.getJSONArray("result");
            Record[] records = new Record[results.length()];

            for (int idx = 0; idx < results.length(); idx++) {
                JSONObject perResult = results.getJSONObject(idx);
                records[idx] = Record.fromJson(perResult);
            }

            QueryInfo queryInfo = null;
            if (result.has("info")) {
                JSONObject infoJson = result.getJSONObject("info");
                Integer overallCount = null;
                if (infoJson.has("count")) {
                    overallCount = infoJson.getInt("count");
                }
                queryInfo = new QueryInfo(overallCount);
            }
            this.onQuerySuccess(records);
            this.onQuerySuccess(records, queryInfo);
        } catch (JSONException e) {
            this.onQueryError(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onQueryError(error);
    }
}
