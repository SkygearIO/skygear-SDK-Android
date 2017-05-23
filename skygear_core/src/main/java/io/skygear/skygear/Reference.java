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
