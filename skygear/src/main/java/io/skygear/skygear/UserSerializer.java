package io.skygear.skygear;

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

        return new User(
                userId,
                userObject.optString("access_token"),
                userObject.optString("username"),
                userObject.optString("email")
        );
    }
}
