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
 * The Skygear UnknownValue Data Type Object Serializer.
 */
public class UnknownValueSerializer {
    /**
     * Serialize a UnknownValue.
     *
     * @param unknownValue the unknown value
     * @return the json object
     */
    public static JSONObject serialize(UnknownValue unknownValue) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$type", "unknown");
            if (unknownValue.underlyingType != null) {
                jsonObject.put("$underlying_type", unknownValue.underlyingType);
            }

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize a UnknownValue from JSON object.
     *
     * @param jsonObject the JSON object
     * @return the UnknownValue
     * @throws JSONException the JSON exception
     */
    public static UnknownValue deserialize(JSONObject jsonObject) throws JSONException {
        String typeValue = jsonObject.getString("$type");
        if (typeValue.equals("unknown")) {
            String underlyingType = null;
            if (jsonObject.has("$underlying_type")) {
                underlyingType = jsonObject.getString("$underlying_type");
            }

            UnknownValue unknownValue = new UnknownValue(underlyingType);
            return unknownValue;
        }

        throw new JSONException("Invalid $type value: " + typeValue);
    }

    /**
     * Determines whether an object is a JSON object in Skygear defined unknown
     * value format.
     *
     * @param object the object
     * @return the boolean
     */
    public static boolean isUnknownValueFormat(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;
            return jsonObject.getString("$type").equals("unknown");
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }
}

