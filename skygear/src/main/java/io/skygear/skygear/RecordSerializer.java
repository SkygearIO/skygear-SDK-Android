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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Skygear Record Serializer.
 */
public class RecordSerializer {

    private static final String RecordSerializationDeprecatedIDKey = "_id";
    private static final String RecordSerializationResponseTypeKey = "_type";
    private static final String RecordSerializationRecordTypeKey = "_recordType";
    private static final String RecordSerializationRecordIDKey = "_recordID";
    private static final String RecordSerializationCreationDatetimeKey = "_created_at";
    private static final String RecordSerializationUpdateDatetimeKey = "_updated_at";
    private static final String RecordSerializationOwnerIDKey = "_ownerID";
    private static final String RecordSerializationCreatorIDKey = "_created_by";
    private static final String RecordSerializationUpdaterIDKey = "_updated_by";
    private static final String RecordSerializationAccessKey = "_access";
    private static final String RecordSerializationTransientKey = "_transient";

    private static final String TAG = "Skygear SDK";
    private static List<String> ReservedKeys = Arrays.asList(
            RecordSerializationDeprecatedIDKey,
            RecordSerializationResponseTypeKey,
            RecordSerializationRecordTypeKey,
            RecordSerializationRecordIDKey,
            RecordSerializationCreationDatetimeKey,
            RecordSerializationUpdateDatetimeKey,
            RecordSerializationOwnerIDKey,
            RecordSerializationCreatorIDKey,
            RecordSerializationUpdaterIDKey,
            RecordSerializationAccessKey,
            RecordSerializationTransientKey
    );

    private static Set<? extends Class> CompatibleValueClasses = new HashSet<>(Arrays.asList(
            /* Primitive types */
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class,

            /* JSON types */
            JSONObject.class,
            JSONArray.class,

            /* Other types */
            Date.class,
            Asset.class,
            Location.class,
            Reference.class,
            UnknownValue.class
    ));

    /**
     * Check if a record type is valid
     *
     * @param type the record type
     * @return the boolean to indicate validity
     */
    static boolean isValidType(String type) {
        return type != null && type.length() > 0 && type.charAt(0) != '_';
    }

    /**
     * Check if an attribute key is valid
     *
     * @param key the attribute key
     * @return the boolean to indicate validity
     */
    static boolean isValidKey(String key) {
        return key != null && key.length() > 0 && !ReservedKeys.contains(key);
    }

    /**
     * Check if an attribute value is compatible
     *
     * @param value the attribute value
     * @return the boolean to indicate compatibility
     */
    static boolean isCompatibleValue(Object value) {
        if (value == null) {
            return false;
        }

        if (value == JSONObject.NULL) {
            return true;
        }

        Class<?> valueClass = value.getClass();

        if (valueClass.isArray()) {
            Object[] valueArray = (Object[]) value;
            for (Object eachItem : valueArray) {
                if (!isCompatibleValue(eachItem)) {
                    return false;
                }
            }

            return true;
        }

        return CompatibleValueClasses.contains(valueClass);
    }

    public static JSONObject serialize(Map<String, Object> data) {
        HashMap<String, Object> recordData = new HashMap<>(data);

        for (String perKey : recordData.keySet()) {
            Object perValue = recordData.get(perKey);

            if (perValue instanceof Date) {
                recordData.put(perKey, DateSerializer.serialize((Date) perValue));
            } else if (perValue instanceof Asset) {
                recordData.put(perKey, AssetSerializer.serialize((Asset) perValue));
            } else if (perValue instanceof Location) {
                recordData.put(perKey, LocationSerializer.serialize((Location) perValue));
            } else if (perValue instanceof Reference) {
                recordData.put(perKey, ReferenceSerializer.serialize((Reference) perValue));
            } else if (perValue instanceof UnknownValue) {
                recordData.put(perKey, UnknownValueSerializer.serialize((UnknownValue) perValue));
            }
        }

        return new JSONObject(recordData);
    }

    /**
     * Serializes a Skygear Record
     *
     * @param record the record
     * @return the JSON object
     */
    public static JSONObject serialize(Record record) {
        try {
            JSONObject jsonObject = RecordSerializer.serialize(record.data);

            jsonObject.put(RecordSerializationDeprecatedIDKey, String.format(
                    "%s/%s",
                    record.getType(),
                    record.getId()
            ));
            jsonObject.put(RecordSerializationRecordTypeKey, record.getType());
            jsonObject.put(RecordSerializationRecordIDKey, record.getId());
            if (record.createdAt != null) {
                jsonObject.put(
                        RecordSerializationCreationDatetimeKey,
                        DateSerializer.stringFromDate(record.createdAt)
                );
            }
            if (record.updatedAt != null) {
                jsonObject.put(
                        RecordSerializationUpdateDatetimeKey,
                        DateSerializer.stringFromDate(record.updatedAt)
                );
            }
            if (record.creatorId != null) {
                jsonObject.put(RecordSerializationCreatorIDKey, record.creatorId);
            }
            if (record.updaterId != null) {
                jsonObject.put(RecordSerializationUpdaterIDKey, record.updaterId);
            }
            if (record.ownerId != null) {
                jsonObject.put(RecordSerializationOwnerIDKey, record.ownerId);
            }

            if (record.getAccess() != null) {
                jsonObject.put(
                        RecordSerializationAccessKey,
                        AccessControlSerializer.serialize(record.getAccess())
                );
            }

            // handle _transient
            Map<String, Object> transientMap = record.getTransient();
            if (transientMap.size() > 0) {
                JSONObject transientObject = new JSONObject();
                for (String perKey : transientMap.keySet()) {
                    Object perValue = transientMap.get(perKey);
                    if (perValue instanceof Record) {
                        transientObject.put(perKey, RecordSerializer.serialize((Record) perValue));
                    } else {
                        transientObject.put(perKey, perValue);
                    }
                }

                jsonObject.put(RecordSerializationTransientKey, transientObject);
            }

            return jsonObject;
        } catch (JSONException e) {
            Log.w(TAG, "Fail to serialize record object", e);
        }

        return null;
    }

    /**
     * Deserialize a Skygear Record.
     *
     * @param jsonObject the JSON object
     * @return the Skygear Record
     * @throws JSONException the JSON exception
     */
    public static Record deserialize(JSONObject jsonObject) throws JSONException {
        RecordIdentifier identifier = RecordSerializer.deserializeRecordIdentifer(jsonObject);
        Record record = new Record(identifier.type, identifier.id);

        // handle _create_at
        if (jsonObject.has(RecordSerializationCreationDatetimeKey)) {
            String createdAtString = jsonObject.getString(RecordSerializationCreationDatetimeKey);
            record.createdAt = DateSerializer.dateFromString(createdAtString);
        }

        // handle _updated_at
        if (jsonObject.has(RecordSerializationUpdateDatetimeKey)) {
            String updatedAtString = jsonObject.getString(RecordSerializationUpdateDatetimeKey);
            record.updatedAt = DateSerializer.dateFromString(updatedAtString);
        }

        // handle _created_by, _updated_by, _ownerID
        if (jsonObject.has(RecordSerializationCreatorIDKey)) {
            record.creatorId = jsonObject.getString(RecordSerializationCreatorIDKey);
        }
        if (jsonObject.has(RecordSerializationUpdaterIDKey)) {
            record.updaterId = jsonObject.getString(RecordSerializationUpdaterIDKey);
        }
        if (jsonObject.has(RecordSerializationOwnerIDKey)) {
            record.ownerId = jsonObject.getString(RecordSerializationOwnerIDKey);
        }

        // handle _access
        JSONArray accessJsonArray = null;
        if (!jsonObject.isNull(RecordSerializationAccessKey)) {
            accessJsonArray = jsonObject.getJSONArray(RecordSerializationAccessKey);
        }

        // handle _transient
        if (!jsonObject.isNull(RecordSerializationTransientKey)) {
            JSONObject transientObject = jsonObject.getJSONObject(RecordSerializationTransientKey);
            Iterator<String> transientKeys = transientObject.keys();

            while(transientKeys.hasNext()) {
                String perKey = transientKeys.next();

                // server will return { "some-key": null } when "some-key" is not a relation
                if (!transientObject.isNull(perKey)) {
                    Object perValue = transientObject.get(perKey);
                    if (perValue instanceof JSONObject) {
                        record.transientMap.put(
                                perKey,
                                RecordSerializer.deserialize((JSONObject) perValue)
                        );
                    } else {
                        record.transientMap.put(perKey, perValue);
                    }
                }
            }
        }

        record.access = AccessControlSerializer.deserialize(accessJsonArray);

        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String nextKey = keys.next();
            if (!ReservedKeys.contains(nextKey)) {
                Object nextValue = jsonObject.get(nextKey);

                if (DateSerializer.isDateFormat(nextValue)) {
                    record.set(nextKey, DateSerializer.deserialize((JSONObject) nextValue));
                } else if (AssetSerializer.isAssetFormat(nextValue)) {
                    record.set(nextKey, AssetSerializer.deserialize((JSONObject) nextValue));
                } else if (LocationSerializer.isLocationFormat(nextValue)) {
                    record.set(nextKey, LocationSerializer.deserialize((JSONObject) nextValue));
                } else if (ReferenceSerializer.isReferenceFormat(nextValue)) {
                    record.set(nextKey, ReferenceSerializer.deserialize((JSONObject) nextValue));
                } else if (UnknownValueSerializer.isUnknownValueFormat(nextValue)) {
                    record.set(nextKey, UnknownValueSerializer.deserialize((JSONObject) nextValue));
                } else {
                    record.set(nextKey, nextValue);
                }
            }
        }

        return record;
    }

    /**
     * RecordIdentifier is a data class representing the identifier of a record, which includes
     * the type and the ID of the record.
     *
     * RecordIdentifier is only used within this package.
     */
    static class RecordIdentifier {
        final String type;
        final String id;

        RecordIdentifier(String type, String id) {
            this.type = type;
            this.id = id;
        }
    }

    static RecordIdentifier deserializeRecordIdentifer(JSONObject jsonObject)
            throws JSONException
    {
        String recordType;
        String recordID;
        try {
            recordType = jsonObject.getString(RecordSerializationRecordTypeKey);
            recordID = jsonObject.getString(RecordSerializationRecordIDKey);
        } catch (JSONException e) {
            String typedId = jsonObject.getString(RecordSerializationDeprecatedIDKey);
            String[] split = typedId.split("/", 2);

            if (split.length != 2 || split[0].length() == 0 || split[1].length() == 0) {
                throw new JSONException(String.format(
                        "%s and / or %s is malformed",
                        RecordSerializationRecordTypeKey,
                        RecordSerializationRecordIDKey
                ));
            }

            recordType = split[0];
            recordID = split[1];
        }

        return new RecordIdentifier(recordType, recordID);
    }
}
