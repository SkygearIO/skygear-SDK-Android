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
 * UnknownValue indicates that the value is of unknown type to Skygear.
 *
 * This usually occurs when the database contains data that is not managed
 * by Skygear.
 *
 * You should not instantiate an instance of this class.
 */
public class UnknownValue {
    /**
     * The name of the underlying data type.
     */
    String underlyingType;

    /**
     * Instantiates a new unknown value.
     *
     * @param underlyingType the name of the underlying data type
     */
    public UnknownValue(String underlyingType) {
        super();
        this.underlyingType = underlyingType;
    }

    /**
     * Gets underlying data type.
     *
     * @return the underlying data type
     */
    public String getUnderlyingType() {
        return underlyingType;
    }

    /**
     * Serialize the unknown value.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        return UnknownValueSerializer.serialize(this);
    }

    /**
     * Deserializes the unknown value.
     *
     * @param jsonObject the JSON object
     * @return the unknown value
     * @throws JSONException the json exception
     */
    public static UnknownValue fromJson(JSONObject jsonObject) throws JSONException {
        return UnknownValueSerializer.deserialize(jsonObject);
    }
}
