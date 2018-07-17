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
 * The Skygear Record Reference Serializer.
 * <p>
 * This class converts between record reference object and JSON object in Skygear defined format.
 */
public class ReferenceSerializer {

    /**
     * Serializes a record reference
     *
     * @param reference the reference
     * @return the json object
     */
    public static JSONObject serialize(Reference reference) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$type", "ref");
            jsonObject.put("$id", reference.getType() + '/' + reference.getId());
            jsonObject.put("$recordType", reference.getType());
            jsonObject.put("$recordID", reference.getId());

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize a record reference from JSON object.
     *
     * @param jsonObject the JSON object
     * @return the record reference
     * @throws JSONException the json exception
     */
    public static Reference deserialize(JSONObject jsonObject) throws JSONException {
        String typeValue = jsonObject.getString("$type");
        if (typeValue.equals("ref")) {
            String recordType, recordID;
            try {
                recordType = jsonObject.getString("$recordType");
                recordID = jsonObject.getString("$recordID");
            } catch (JSONException e) {
                String typedId = jsonObject.getString("$id");
                String[] split = typedId.split("/", 2);

                if (split.length < 2 || split[0].length() == 0 || split[1].length() == 0) {
                    throw new JSONException("$id field is malformed");
                }

                recordType = split[0];
                recordID = split[1];
            }

            return new Reference(recordType, recordID);
        }

        throw new JSONException("Invalid $type value: " + typeValue);
    }

    /**
     * Determines whether an object is a JSON object in Skygear defined record reference format.
     *
     * @param object the object
     * @return the boolean
     */
    public static boolean isReferenceFormat(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject == null) {
                return false;
            }

            if (!jsonObject.getString("$type").equals("ref")) {
                return false;
            }

            try {
                String recordType = jsonObject.getString("$recordType");
                String recordID = jsonObject.getString("$recordID");

                return recordType.length() > 0 && recordID.length() > 0;
            } catch (JSONException e) {
                String typedId = jsonObject.getString("$id");
                String[] split = typedId.split("/", 2);

                return split.length >= 2 && split[0].length() > 0 && split[1].length() > 0;
            }
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }
}
