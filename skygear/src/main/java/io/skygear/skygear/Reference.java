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
 * The Skygear Record Reference.
 */
public class Reference {
    /**
     * The Record Id.
     */
    String id;

    /**
     * The Record Type.
     */
    String type;

    /**
     * Instantiates a new Record Reference.
     *
     * @param record the record
     */
    public Reference(Record record) {
        this(record.getType(), record.getId());
    }

    /**
     * Instantiates a new Record Reference.
     *
     * @param type the record type
     * @param id   the record id
     */
    public Reference(String type, String id) {
        super();

        this.id = id;
        this.type = type;
    }

    /**
     * Gets record id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets record type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Serialize the record reference.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        return ReferenceSerializer.serialize(this);
    }

    /**
     * Deserializes the record reference
     *
     * @param jsonObject the JSON object
     * @return the record reference
     * @throws JSONException the json exception
     */
    public static Reference fromJson(JSONObject jsonObject) throws JSONException {
        return ReferenceSerializer.deserialize(jsonObject);
    }
}
