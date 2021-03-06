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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
public class Record implements Parcelable {
    private static final String TAG = "Skygear SDK";

    String id;
    String type;

    Date createdAt;
    Date updatedAt;

    AccessControl access;

    String creatorId;
    String updaterId;
    String ownerId;

    boolean deleted;

    Map<String, Object> transientMap;

    Map<String, Object> data;

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     */
    public Record(String type) {
        this(type, null, null);
    }

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     * @param data the data
     */
    public Record(String type, Map<String, Object>data) {
        this(type, null, data);
    }

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     * @param id the record id
     */
    public Record(String type, String id) {
        this(type, id, null);
    }

    /**
     * Instantiates a new Skygear Record.
     *
     * @param type the record type
     * @param id the record id
     * @param data the data
     */
    public Record(String type, String id, Map<String, Object>data) {
        super();

        if (!RecordSerializer.isValidType(type)) {
            throw new InvalidParameterException("Invalid record type");
        }

        this.type = type;
        if (id == null || id.isEmpty()) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = id;
        }

        this.ownerId = null;
        this.creatorId = null;
        this.updaterId = null;
        this.createdAt = null;
        this.updatedAt = null;

        this.deleted = false;

        this.access = AccessControl.defaultAccessControl();

        this.transientMap = new HashMap<>();

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
    public HashMap<String, Object> getData() {
        return new HashMap<>(this.data);
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
     * Gets the transient.
     *
     * @return the transient map
     */
    public Map<String, Object> getTransient() {
        return new HashMap<>(this.transientMap);
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
     * Sets read write access for a user.
     *
     * @param user the user
     */
    public void setReadWriteAccess(Record user) {
        if (!user.getType().equals("user")) {
            throw new InvalidParameterException("Record type should be user");
        }

        this.setReadWriteAccess(user.getId());
    }

    /**
     * Sets read only for a user.
     *
     * @param user the user
     */
    public void setReadOnly(Record user) {
        if (!user.getType().equals("user")) {
            throw new InvalidParameterException("Record type should be user");
        }

        this.setReadOnly(user.getId());
    }

    /**
     * Sets no access for a user.
     *
     * @param user the user
     */
    public void setNoAccess(Record user) {
        if (!user.getType().equals("user")) {
            throw new InvalidParameterException("Record type should be user");
        }

        this.setNoAccess(user.getId());
    }

    /**
     * Sets read write access for a user id.
     *
     * @param userId the user id
     */
    public void setReadWriteAccess(String userId) {
        this.access.addEntry(new AccessControl.Entry(userId, AccessControl.Level.READ_WRITE));
    }

    /**
     * Sets read only for a user id.
     *
     * @param userId the user id
     */
    public void setReadOnly(String userId) {
        this.access.removeEntry(new AccessControl.Entry(userId, AccessControl.Level.READ_WRITE));
        this.access.addEntry(new AccessControl.Entry(userId, AccessControl.Level.READ_ONLY));
    }

    /**
     * Sets no access for a user id.
     *
     * @param userId the user id
     */
    public void setNoAccess(String userId) {
        this.access.clearEntries(userId);
    }

    /**
     * Sets read write access for a role.
     *
     * @param role the role
     */
    public void setReadWriteAccess(Role role) {
        this.access.addEntry(new AccessControl.Entry(role, AccessControl.Level.READ_WRITE));
    }

    /**
     * Sets read only for a role.
     *
     * @param role the role
     */
    public void setReadOnly(Role role) {
        this.access.removeEntry(new AccessControl.Entry(role, AccessControl.Level.READ_WRITE));
        this.access.addEntry(new AccessControl.Entry(role, AccessControl.Level.READ_ONLY));
    }

    /**
     * Sets no access for a role.
     *
     * @param role the role
     */
    public void setNoAccess(Role role) {
        this.access.clearEntries(role);
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
     * Checks whether it is writable for a user.
     *
     * @param user the user
     * @return the boolean indicating whether it is writable for a user.
     */
    public boolean isWritable(Record user) {
        if (!user.getType().equals("user")) {
            throw new InvalidParameterException("Record type should be user");
        }

        return this.isWritable(user.getId());
    }

    /**
     * Checks whether it is readable for a user.
     *
     * @param user the user
     * @return the boolean indicating whether it is readable for a user.
     */
    public boolean isReadable(Record user) {
        if (!user.getType().equals("user")) {
            throw new InvalidParameterException("Record type should be user");
        }

        return this.isReadable(user.getId());
    }

    /**
     * Checks whether it is writable for a user id.
     *
     * @param userId the user id
     * @return the boolean indicating whether it is writable for a user.
     */
    public boolean isWritable(String userId) {
        AccessControl.Level level = this.getAccess().getAccess(userId).getLevel();

        return level == AccessControl.Level.READ_WRITE;
    }

    /**
     * Checks whether it is readable for a user id.
     *
     * @param userId the user id
     * @return the boolean indicating whether it is readable for a user.
     */
    public boolean isReadable(String userId) {
        AccessControl.Level level = this.getAccess().getAccess(userId).getLevel();

        return level == AccessControl.Level.READ_WRITE || level == AccessControl.Level.READ_ONLY;
    }

    /**
     * Checks whether it is writable for a role.
     *
     * @param role  the role
     * @return the boolean indicating whether it is writable for a role.
     */
    public boolean isWritable(Role role) {
        AccessControl.Level level = this.getAccess().getAccess(role).getLevel();

        return level == AccessControl.Level.READ_WRITE;
    }

    /**
     * Checks whether it is readable for a role.
     *
     * @param role the role
     * @return the boolean indicating whether it is readable for a user.
     */
    public boolean isReadable(Role role) {
        AccessControl.Level level = this.getAccess().getAccess(role).getLevel();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.toJson().toString());
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            String jsonString = in.readString();

            try {
                return Record.fromJson(new JSONObject(jsonString));
            } catch (JSONException e) {
                Log.e(TAG, "Fail to create record from Parcel", e);
            }

            return null;
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
