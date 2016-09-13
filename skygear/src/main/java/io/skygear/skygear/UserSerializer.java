package io.skygear.skygear;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;

/**
 * The Skygear User Serializer.
 */
public class UserSerializer {
    /**
     * Serializes a Skygear User to JSON Object.
     *
     * @param user the user
     * @return the JSON object
     */
    static JSONObject serialize(User user) {
        try {
            JSONObject userObject = new JSONObject();

            String userId = user.getId();
            if (userId != null) {
                userObject.put("_id", userId);
            } else {
                throw new InvalidParameterException("Invalid User: no user id");
            }

            userObject.put("access_token", user.getAccessToken());
            userObject.put("username", user.getUsername());
            userObject.put("email", user.getEmail());

            if (user.lastLoginTime != null) {
                userObject.put(
                        "last_login_at",
                        RecordSerializer.dateTimeFormatter.print(new DateTime(user.lastLoginTime))
                );
            }

            if (user.lastSeenTime != null) {
                userObject.put(
                        "last_seen_at",
                        RecordSerializer.dateTimeFormatter.print(new DateTime(user.lastSeenTime))
                );
            }

            JSONArray roles = new JSONArray();
            for (Role perRole : user.roles) {
                roles.put(perRole.getName());
            }

            userObject.put("roles", roles);

            return userObject;
        } catch (JSONException e) {
            throw new InvalidParameterException(e.getMessage());
        }
    }

    /**
     * Deserializes a Skygear User from JSON Object
     *
     * @param userObject the JSON object
     * @return the user
     * @throws JSONException the json exception
     */
    static User deserialize(JSONObject userObject) throws JSONException {
        String userId;

        if (userObject.has("_id")) {
            userId = userObject.getString("_id");
        } else if (userObject.has("user_id")) {
            userId = userObject.getString("user_id");
        } else {
            throw new JSONException("Missing _id or user_id field");
        }

        User theUser = new User(
                userId,
                userObject.optString("access_token"),
                userObject.optString("username"),
                userObject.optString("email")
        );

        if (userObject.has("last_login_at")) {
            String lastLoginAt = userObject.getString("last_login_at");
            DateTime lastLoginAtDateTime = RecordSerializer.dateTimeFormatter.parseDateTime(lastLoginAt);
            theUser.lastLoginTime = lastLoginAtDateTime.toDate();
        }

        if (userObject.has("last_seen_at")) {
            String lastSeenAt = userObject.getString("last_seen_at");
            DateTime lastSeenAtDatetime = RecordSerializer.dateTimeFormatter.parseDateTime(lastSeenAt);
            theUser.lastSeenTime = lastSeenAtDatetime.toDate();
        }

        JSONArray userRoleArray = userObject.optJSONArray("roles");
        if (userRoleArray != null) {
            int roleCount = userRoleArray.length();
            for (int idx = 0; idx < roleCount; idx++) {
                String perRoleName = userRoleArray.getString(idx);
                theUser.addRole(new Role(perRoleName));
            }
        }

        return theUser;
    }
}
