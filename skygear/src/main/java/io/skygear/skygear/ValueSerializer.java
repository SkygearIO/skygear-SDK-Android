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

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Skygear Value Serializer.
 */
public class ValueSerializer {
    /**
     * Serializes a Skygear Value
     *
     * @param value the value to be serialized
     * @return the JSON object
     */

    protected static Object serialize(Object value) throws JSONException {
        if (value == null || JSONObject.NULL.equals(value)) {
            return JSONObject.NULL;
        } else if (value instanceof JSONObject || value instanceof JSONArray) {
            return value;
        } else if (value instanceof Number || value instanceof Boolean || value instanceof String || value instanceof Character) {
            return value;
        } else if (value instanceof Map) {
            return ValueSerializer.serialize((Map)value);
        } else if (value instanceof List) {
            return ValueSerializer.serialize((List) value);
        } else if (value instanceof Object[]) {
            return ValueSerializer.serialize((Object[]) value);
        } else if (value instanceof Date) {
            return ValueSerializer.serialize((Date) value);
        } else if (value instanceof Asset) {
            return ValueSerializer.serialize((Asset) value);
        } else if (value instanceof Location) {
            return ValueSerializer.serialize((Location) value);
        } else if (value instanceof Reference) {
            return ValueSerializer.serialize((Reference) value);
        } else if (value instanceof UnknownValue) {
            return ValueSerializer.serialize((UnknownValue) value);
        } else if (value instanceof Record) {
            return ValueSerializer.serialize((Record) value);
        } else {
            throw new JSONException(String.format("Class %s cannot be serialized.",
                    value.getClass().getName()));
        }
    }

    public static JSONObject serialize(Date value) throws JSONException {
        return DateSerializer.serialize(value);
    }

    public static JSONObject serialize(Asset value) throws JSONException {
        return AssetSerializer.serialize(value);
    }

    public static JSONObject serialize(Location value) throws JSONException {
        return LocationSerializer.serialize(value);
    }

    public static JSONObject serialize(Reference value) throws JSONException {
        return ReferenceSerializer.serialize(value);
    }

    public static JSONObject serialize(UnknownValue value) throws JSONException {
        return UnknownValueSerializer.serialize(value);
    }

    public static JSONObject serialize(Record value) throws JSONException {
        JSONObject result = new JSONObject();
        result.put("$type", "record");
        result.put("$record", RecordSerializer.serialize(value));
        return result;
    }

    public static JSONArray serialize(Object[] value) throws JSONException {
        return ValueSerializer.serialize(Arrays.asList(value));
    }

    public static JSONArray serialize(List value) throws JSONException {
        JSONArray result = new JSONArray();
        for (Object obj : value) {
            result.put(ValueSerializer.serialize(obj));
        }
        return result;
    }

    public static <T extends Object> JSONObject serialize(Map<String, T> value) throws JSONException {
        JSONObject result = new JSONObject();
        for (String key : value.keySet()) {
            result.put(key, ValueSerializer.serialize(value.get(key)));
        }
        return result;
    }

    /**
     * Deserialize a Skygear Value.
     *
     * @param jsonObject the JSON object
     * @return a Skygear value
     * @throws JSONException the JSON exception
     */
    public static Object deserialize(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        } else if (DateSerializer.isDateFormat(jsonObject)) {
            return DateSerializer.deserialize(jsonObject);
        } else if (AssetSerializer.isAssetFormat(jsonObject)) {
            return AssetSerializer.deserialize(jsonObject);
        } else if (LocationSerializer.isLocationFormat(jsonObject)) {
            return LocationSerializer.deserialize(jsonObject);
        } else if (ReferenceSerializer.isReferenceFormat(jsonObject)) {
            return ReferenceSerializer.deserialize(jsonObject);
        } else if (UnknownValueSerializer.isUnknownValueFormat(jsonObject)) {
            return UnknownValueSerializer.deserialize(jsonObject);
        } else if (jsonObject.optString("$type").equals("record")) {
            return RecordSerializer.deserialize(jsonObject.getJSONObject("$record"));
        } else {
            Map<String, Object> result = new HashMap<String, Object>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object obj = jsonObject.get(key);
                result.put(key, ValueSerializer.deserialize(obj));
            }
            return result;
        }
    }

    public static List deserialize(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        List result = new ArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            result.add(ValueSerializer.deserialize(jsonArray.get(i)));
        }
        return result;
    }

    protected static Object deserialize(Object anyJSONObject) throws JSONException {
        if (anyJSONObject instanceof JSONArray) {
            return ValueSerializer.deserialize((JSONArray)anyJSONObject);
        } else if (anyJSONObject instanceof JSONObject) {
            return ValueSerializer.deserialize((JSONObject)anyJSONObject);
        } else if (JSONObject.NULL.equals(anyJSONObject)) {
            return null;
        } else {
            return anyJSONObject;
        }
    }
}
