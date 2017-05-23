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
