package io.skygear.skygear;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
    private static List<? extends Class> CompatibleValueClasses = Arrays.asList(
            Boolean.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Short.class,
            String.class
    );

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
     * @return the JSON string
     */
    static String serialize(Record record) {
        try {
            JSONObject jsonObject = new JSONObject(record.data);
            jsonObject.put("_id", String.format("%s/%s", record.type, record.id));

            // TODO: Handle ACL (_access)

            return jsonObject.toString();
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to serialize record object");
        }

        return null;
    }

    /**
     * Deserialize a Skygear Record.
     *
     * @param jsonString the JSON string
     * @return the Skygear Record
     * @throws JSONException the JSON exception
     */
    static Record deserialize(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        String typedId = (String) jsonObject.remove("_id");
        String[] split = typedId.split("/", 2);

        if (split.length < 2 || split[1].length() == 0) {
            throw new InvalidParameterException("_id field is malformed");
        }

        // handle _id
        Record record = new Record(split[0]);
        record.id = split[1];

        // handle _create_at
        String createdAtString = jsonObject.getString("_created_at");
        DateTime createdAtDatetime = ISODateTimeFormat.dateTime().parseDateTime(createdAtString);
        record.createdAt = createdAtDatetime.toDate();

        // handle _updated_at
        String updatedAtString = jsonObject.getString("_updated_at");
        DateTime updatedAtDatetime = ISODateTimeFormat.dateTime().parseDateTime(updatedAtString);
        record.updatedAt = updatedAtDatetime.toDate();

        // handler _created_by, _updated_by, _ownerID
        record.creatorId = jsonObject.getString("_created_by");
        record.updaterId = jsonObject.getString("_updated_by");
        record.ownerId = jsonObject.getString("_ownerID");

        // TODO: Handle ACL (_access)

        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext()) {
            String nextKey = keys.next();
            if (!ReservedKeys.contains(nextKey)) {
                record.set(nextKey, jsonObject.get(nextKey));
            }
        }

        return record;
    }
}
