package io.skygear.skygear;

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
    User(String userId, String accessToken) {
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
    User(String userId, String accessToken, String username, String email) {
        super();

        this.userId = userId;
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }
}
