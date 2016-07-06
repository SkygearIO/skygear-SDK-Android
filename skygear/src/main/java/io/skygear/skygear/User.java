package io.skygear.skygear;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear User Model.
 */
public class User {
    /**
     * The User id.
     */
    public final String userId;
    /**
     * The Access token.
     */
    public final String accessToken;
    /**
     * The Username.
     */
    public final String username;
    /**
     * The Email.
     */
    public final String email;

    /**
     * Instantiates a new Skygear User.
     *
     * @param userId      the user id
     * @param accessToken the access token
     */
    public User(String userId, String accessToken) {
        this(userId, accessToken, null, null);
    }

    /**
     * Instantiates a new Skygear User.
     *
     * @param userId      the user id
     * @param accessToken the access token
     * @param username    the username
     * @param email       the email
     */
    public User(String userId, String accessToken, String username, String email) {
        super();

        this.userId = userId;
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }

    /**
     * Instantiates a new Skygear User from JSON String
     *
     * @param json the json string
     * @return the skygear user
     * @throws JSONException the json exception
     *
     * FIXME: Change to use JSONObject as media
     */
    public static User fromJsonString(String json) throws JSONException {
        JSONObject currentUserJson = new JSONObject(json);
        String userId = currentUserJson.getString("user_id");
        String accessToken = currentUserJson.getString("access_token");
        String username = null;
        String email = null;

        if (currentUserJson.has("username")) {
            username = currentUserJson.getString("username");
        }

        if (currentUserJson.has("email")) {
            email = currentUserJson.getString("email");
        }

        return new User(userId, accessToken, username, email);
    }

    /**
     * Encode a Skygear User to JSON String
     *
     * @return the json string
     *
     * FIXME: Change to use JSONObject as media
     */
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", this.userId);
            jsonObject.put("access_token", this.accessToken);

            if (this.username != null) {
                jsonObject.put("username", this.username);
            }

            if (this.email != null) {
                jsonObject.put("email", this.email);
            }
        } catch (JSONException e) {
            Log.w("Skygear SDK", "Fail to encode user object");
        }

        return jsonObject.toString();
    }
}
