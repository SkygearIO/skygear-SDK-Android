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
            String typedId = jsonObject.getString("$id");
            String[] split = typedId.split("/", 2);

            if (split.length < 2 || split[1].length() == 0) {
                throw new JSONException("$id field is malformed");
            }

            return new Reference(split[0], split[1]);
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
            return jsonObject.getString("$type").equals("ref") &&
                    !jsonObject.isNull("$id");
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }
}
