package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The Skygear Record.
 */
public class Record {
    static AccessControl defaultAccessControl = null;

    String id;
    String type;

    Date createdAt;
    Date updatedAt;

    AccessControl access;

    String creatorId;
    String updaterId;
    String ownerId;

    HashMap<String, Object> data;

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     */
    public Record(String type) {
        this(type, null);
    }

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     * @param data the data
     */
    public Record(String type, Map<String, Object>data) {
        super();

        if (!RecordSerializer.isValidType(type)) {
            throw new InvalidParameterException("Invalid record type");
        }

        this.id = UUID.randomUUID().toString();
        this.type = type;

        this.ownerId = null;
        this.creatorId = null;
        this.updaterId = null;
        this.createdAt = null;
        this.updatedAt = null;

        this.access = Record.defaultAccessControl;

        this.data = new HashMap<>();

        if (data != null) {
            this.set(data);
        }
    }

    /**
     * Sets a set of attributes.
     *
     * @param data the set of attributes
     */
    public void set(Map<String, Object>data) {
        for (Map.Entry<String, Object> perEntry : data.entrySet()) {
            this.set(perEntry.getKey(), perEntry.getValue());
        }
    }

    /**
     * Sets an attribute of the record.
     *
     * @param key   the attribute key
     * @param value the attribute value
     */
    public void set(String key, Object value) {
        if (!RecordSerializer.isValidKey(key)) {
            throw new InvalidParameterException(String.format("Invalid key \"%s\"", key));
        }

        if (!RecordSerializer.isCompatibleValue(value)) {
            throw new InvalidParameterException(String.format("Incompatible value for key \"%s\"", key));
        }

        this.data.put(key, value);
    }

    /**
     * Gets an attribute.
     *
     * @param key the attribute key
     * @return the attribute value
     */
    public Object get(String key) {
        return this.data.get(key);
    }

    /**
     * Gets the whole set of attributes.
     * <p>
     * Please be reminded that the cloned map is returned.
     * </p>
     *
     * @return the set of attributes
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> getData() {
        return (HashMap<String, Object>) this.data.clone();
    }

    /**
     * Gets record id.
     *
     * @return the record id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets record type.
     *
     * @return the record type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets creation time.
     *
     * @return the creation time
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets updating time
     *
     * @return the updating time
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Gets creator id.
     *
     * @return the creator id
     */
    public String getCreatorId() {
        return creatorId;
    }

    /**
     * Gets updater id.
     *
     * @return the updater id
     */
    public String getUpdaterId() {
        return updaterId;
    }

    /**
     * Gets owner id.
     *
     * @return the owner id
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Gets access control.
     *
     * @return the access control
     */
    public AccessControl getAccess() {
        return access;
    }

    /**
     * Sets public read-write access.
     */
    public void setPublicReadWrite() {
        this.access.addEntry(new AccessControl.Entry(AccessControl.Level.READ_WRITE));
    }

    /**
     * Sets public read-only access.
     */
    public void setPublicReadOnly() {
        this.access.removeEntry(new AccessControl.Entry(AccessControl.Level.READ_WRITE));
        this.access.addEntry(new AccessControl.Entry(AccessControl.Level.READ_ONLY));
    }

    /**
     * Sets public no access.
     */
    public void setPublicNoAccess() {
        this.access.clearEntries(AccessControl.Entry.Type.PUBLIC);
    }

    /**
     * Checks whether it is public writable.
     *
     * @return the boolean indicating whether it is public writable
     */
    public boolean isPublicWritable() {
        return this.getAccess().getPublicAccess().getLevel() == AccessControl.Level.READ_WRITE;
    }

    /**
     * Checks whether it is public readable
     *
     * @return the boolean indicating whether it is public readable
     */
    public boolean isPublicReadable() {
        AccessControl.Level level = this.getAccess().getPublicAccess().getLevel();

        return level == AccessControl.Level.READ_WRITE || level == AccessControl.Level.READ_ONLY;
    }

    /**
     * Serializes the record.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        return RecordSerializer.serialize(this);
    }

    /**
     * Deserializes the record.
     *
     * @param jsonObject the json object
     * @return the record
     * @throws JSONException the json exception
     */
    public static Record fromJson(JSONObject jsonObject) throws JSONException {
        return RecordSerializer.deserialize(jsonObject);
    }
}
