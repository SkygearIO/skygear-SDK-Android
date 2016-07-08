package io.skygear.skygear;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.Queue;

import io.skygear.skygear.AccessControl.Entry;

/**
 * The Skygear Access Control Serializer.
 */
public class AccessControlSerializer {
    /**
     * Serializes a Skygear Access Control
     *
     * @param access the access control
     * @return the JSON array
     */
    static JSONArray serialize(AccessControl access) {
        if (access == null) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();
        if (access.publicEntryQueue.size() > 0) {
            Entry highestPublicAccess = access.publicEntryQueue.peek();
            JSONObject highestPublicAccessObject = EntrySerializer.serialize(highestPublicAccess);
            if (highestPublicAccessObject != null) {
                jsonArray.put(highestPublicAccessObject);
            }
        }

        for (String perUserId : access.userEntryMap.keySet()) {
            Queue<Entry> perUserEntryQueue = access.userEntryMap.get(perUserId);
            if (perUserEntryQueue.size() > 0) {
                Entry perUserHighestAccess = perUserEntryQueue.peek();
                JSONObject perUserHighestAccessObject
                        = EntrySerializer.serialize(perUserHighestAccess);
                if (perUserHighestAccessObject != null) {
                    jsonArray.put(perUserHighestAccessObject);
                }
            }
        }

        for (String perRoleName : access.roleEntryMap.keySet()) {
            Queue<Entry> perRoleEntryQueue = access.roleEntryMap.get(perRoleName);
            if (perRoleEntryQueue.size() > 0) {
                Entry perRoleHighestAccess = perRoleEntryQueue.peek();
                JSONObject perRoleHighestAccessObject
                        = EntrySerializer.serialize(perRoleHighestAccess);
                if (perRoleHighestAccessObject != null) {
                    jsonArray.put(perRoleHighestAccessObject);
                }
            }
        }

        return jsonArray;
    }

    /**
     * Deserializes a JSON Array to Skygear Access Control.
     *
     * @param jsonArray the JSON array
     * @return the access control
     * @throws JSONException the JSON exception
     */
    static AccessControl deserialize(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }

        AccessControl accessControl = new AccessControl();

        int entryCount = jsonArray.length();
        for (int idx = 0; idx < entryCount; idx++) {
            JSONObject perEntryObject = jsonArray.getJSONObject(idx);
            Entry perEntry = EntrySerializer.deserialize(perEntryObject);

            accessControl.addEntry(perEntry);
        }

        return accessControl;
    }

    /**
     * The Skygear Access Control Entry serializer.
     */
    static class EntrySerializer {
        /**
         * Serializes a Skygear Access Control Entry.
         *
         * @param entry the entry
         * @return the JSON object
         */
        static JSONObject serialize(Entry entry) {
            String levelString = LevelSerializer.serialize(entry.getLevel());
            if (levelString != null) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("level", levelString);

                    Entry.Type entryType = entry.getType();
                    if (entryType == Entry.Type.PUBLIC) {
                        jsonObject.put("public", true);

                        return jsonObject;
                    } else if (entryType == Entry.Type.USER_BASED) {
                        jsonObject.put("user_id", entry.getUserId());

                        return jsonObject;
                    } else if (entryType == Entry.Type.ROLE_BASED) {
                        jsonObject.put("role", entry.getRole().getName());

                        return jsonObject;
                    }
                } catch (JSONException e) {
                    Log.w("Skygear SDK", "Fail to serialize AccessControl Entry", e);
                }
            }

            return null;
        }

        /**
         * Deserializes a JSON Object to a Skygear Access Control Entry
         *
         * @param jsonObject the JSON object
         * @return the access control entry
         * @throws JSONException the JSON exception
         */
        static Entry deserialize(JSONObject jsonObject) throws JSONException {
            Entry entry = null;

            String levelString = null;
            if (jsonObject.has("level") && !jsonObject.isNull("level")) {
                levelString = jsonObject.getString("level");
            }

            AccessControl.Level level = LevelSerializer.deserialize(levelString);

            if (jsonObject.has("public")) {
                boolean isPublic = jsonObject.getBoolean("public");
                if (isPublic) {
                    entry = new Entry(level);
                } else {
                    throw new InvalidParameterException(
                            "Access Control Entry with public = false is not undefined"
                    );
                }
            } else if (jsonObject.has("user_id")) {
                String userId = jsonObject.getString("user_id");
                entry = new Entry(userId, level);
            } else if (jsonObject.has("role")) {
                String roleName = jsonObject.getString("role");
                entry = new Entry(new Role(roleName), level);
            }

            return entry;
        }
    }

    /**
     * The Skygear Access Level Serializer.
     */
    static class LevelSerializer {
        /**
         * Serializes a Skygear Access Level
         *
         * @param level the access level
         * @return the result string
         */
        static String serialize(AccessControl.Level level) {
            switch (level) {
                case READ_WRITE:
                    return "write";
                case READ_ONLY:
                    return "read";
                default:
                    return null;
            }
        }

        /**
         * Deserializes a string to a Skygear Access Level
         *
         * @param levelString the string
         * @return the Skygear Access Level
         */
        static AccessControl.Level deserialize(String levelString) {
            if (levelString == null) {
                return AccessControl.Level.NO_ACCESS;
            }

            String levelStringLowerCase = levelString.toLowerCase();
            switch (levelStringLowerCase) {
                case "write":
                    return AccessControl.Level.READ_WRITE;
                case "read":
                    return AccessControl.Level.READ_ONLY;
                default:
                    throw new InvalidParameterException("Unknown Access Level: " + levelString);
            }
        }
    }
}
