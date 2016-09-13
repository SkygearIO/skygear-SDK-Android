package io.skygear.skygear;

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
import java.util.Set;

/**
 * The Skygear Record Serializer.
 */
public class RecordSerializer {
    private static List<String> ReservedKeys = Arrays.asList(
            "_id",
            "_type",
            "_created_at",
            "_updated_at",
            "_ownerID",
            "_created_by",
            "_updated_by",
            "_access"
    );

    private static Set<? extends Class> CompatibleValueClasses = new HashSet<>(Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class,
            JSONObject.class,
            JSONArray.class,
            Date.class
    ));

    /**
     * The Datetime Formatter for Skygear in ISO Format.
     */
    static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

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

    /**
     * Serializes a Skygear Record
     *
     * @param record the record
     * @return the JSON object
     */
    static JSONObject serialize(Record record) {
        try {
            HashMap<String, Object> recordData = record.data;

            for (String perKey : recordData.keySet()) {
                Object perValue = recordData.get(perKey);

                if (perValue instanceof Date) {
                    recordData.put(perKey, DateSerializer.serialize((Date) perValue));
                }
            }

            JSONObject jsonObject = new JSONObject(recordData);
            jsonObject.put("_id", String.format("%s/%s", record.type, record.id));
            jsonObject.put("_created_at", RecordSerializer.dateTimeFormatter.print(new DateTime(record.createdAt)));
            jsonObject.put("_updated_at", RecordSerializer.dateTimeFormatter.print(new DateTime(record.updatedAt)));
            jsonObject.put("_created_by", record.creatorId);
            jsonObject.put("_updated_by", record.updaterId);
            jsonObject.put("_ownerID", record.ownerId);

            jsonObject.put("_access", AccessControlSerializer.serialize(record.getAccess()));

            return jsonObject;
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to serialize record object", e);
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
    static Record deserialize(JSONObject jsonObject) throws JSONException {
        String typedId = (String) jsonObject.remove("_id");
        String[] split = typedId.split("/", 2);

        if (split.length < 2 || split[1].length() == 0) {
            throw new InvalidParameterException("_id field is malformed");
        }

        // handle _id
        Record record = new Record(split[0]);
        record.id = split[1];

        // handle _create_at
        if (jsonObject.has("_created_at")) {
            String createdAtString = jsonObject.getString("_created_at");
            DateTime createdAtDatetime = RecordSerializer.dateTimeFormatter.parseDateTime(createdAtString);
            record.createdAt = createdAtDatetime.toDate();
        }

        // handle _updated_at
        if (jsonObject.has("_updated_at")) {
            String updatedAtString = jsonObject.getString("_updated_at");
            DateTime updatedAtDatetime = RecordSerializer.dateTimeFormatter.parseDateTime(updatedAtString);
            record.updatedAt = updatedAtDatetime.toDate();
        }

        // handler _created_by, _updated_by, _ownerID
        record.creatorId = jsonObject.optString("_created_by");
        record.updaterId = jsonObject.optString("_updated_by");
        record.ownerId = jsonObject.optString("_ownerID");

        JSONArray accessJsonArray = null;
        if (!jsonObject.isNull("_access")) {
            accessJsonArray = jsonObject.getJSONArray("_access");
        }

        record.access = AccessControlSerializer.deserialize(accessJsonArray);

        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String nextKey = keys.next();
            if (!ReservedKeys.contains(nextKey)) {
                Object nextValue = jsonObject.get(nextKey);

                if (DateSerializer.isDateFormat(nextValue)) {
                    record.set(nextKey, DateSerializer.deserialize((JSONObject) nextValue));
                } else {
                    record.set(nextKey, nextValue);
                }
            }
        }

        return record;
    }

    /**
     * The Skygear Date Serializer.
     *
     * This class converts between date object and JSON object in Skygear defined format.
     */
    static class DateSerializer {
        /**
         * Serialize a date object.
         *
         * @param date the date object
         * @return the json object
         */
        static JSONObject serialize(Date date) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("$type", "date");
                jsonObject.put("$date", RecordSerializer.dateTimeFormatter.print(new DateTime(date)));

                return jsonObject;
            } catch (JSONException e) {
                return null;
            }
        }

        /**
         * Deserialize json object.
         *
         * @param dateJsonObject the date json object
         * @return the date object
         * @throws JSONException the json exception
         */
        static Date deserialize(JSONObject dateJsonObject) throws JSONException {
            String typeValue = dateJsonObject.getString("$type");
            if (typeValue.equals("date")) {
                String dateString = dateJsonObject.getString("$date");

                return RecordSerializer.dateTimeFormatter.parseDateTime(dateString).toDate();
            }

            throw new JSONException("Invalid $type value: " + typeValue);
        }

        /**
         * Determines whether an object is a JSON object in Skygear defined date format.
         *
         * @param object the object
         * @return the indicating boolean
         */
        static boolean isDateFormat(Object object) {
            try {
                JSONObject jsonObject = (JSONObject) object;

                return jsonObject.getString("$type").equals("date") &&
                        jsonObject.getString("$date").length() > 0;
            } catch (ClassCastException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }
    }
}
