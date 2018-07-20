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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Error Serializer.
 * <p>
 * This class converts between error object and JSON object in Skygear defined format.
 */
public class ErrorSerializer {

    private static final String ErrorSerializationCodeKey = "code";
    private static final String ErrorSerializationNameKey = "name";
    private static final String ErrorSerializationMessageKey = "message";
    private static final String ErrorSerializationExtraInfoKey = "info";

    /**
     * Serializes an error object
     *
     * @param errorObject the error object
     * @return the json object
     */
    public static JSONObject serialize(Error errorObject) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(ErrorSerializationCodeKey, errorObject.getCodeValue());
            jsonObject.put(ErrorSerializationNameKey, errorObject.getName());
            jsonObject.put(ErrorSerializationMessageKey, errorObject.getDetailMessage());
            
            JSONObject infoObject = errorObject.getInfo();
            if (infoObject != null) {
                jsonObject.put(ErrorSerializationExtraInfoKey, infoObject);
            }

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize an error from JSON object.
     *
     * @param jsonObject the JSON object
     * @return the error object
     * @throws JSONException the json exception
     */
    public static Error deserialize(JSONObject jsonObject) throws JSONException {

        int errorCodeValue = jsonObject.getInt(ErrorSerializationCodeKey);
        String errorName = jsonObject.optString(ErrorSerializationNameKey);
        String errorString = jsonObject.optString(ErrorSerializationMessageKey);
        JSONObject errorInfo = jsonObject.optJSONObject(ErrorSerializationExtraInfoKey);

        return new Error(errorCodeValue, errorName, errorString, errorInfo);
    }
}
