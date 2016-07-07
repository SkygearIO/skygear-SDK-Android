package io.skygear.skygear;

/**
 * The Skygear User Model.
 */
public class User {
    /**
     * The User id.
     */
    final String id;
    /**
     * The Access token.
     */
    String accessToken;
    /**
     * The Username.
     */
    String username;
    /**
     * The Email.
     */
    String email;

    /**
     * Instantiates a new Skygear User.
     *
     * @param id      the user id
     * @param accessToken the access token
     */
    public User(String id, String accessToken) {
        this(id, accessToken, null, null);
    }

    /**
     * Instantiates a new Skygear User.
     *
     * @param id      the user id
     * @param accessToken the access token
     * @param username    the username
     * @param email       the email
     */
    public User(String id, String accessToken, String username, String email) {
        super();

        this.id = id;
        this.accessToken = accessToken;
        this.username = username;
        this.email = email;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }
}
