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

