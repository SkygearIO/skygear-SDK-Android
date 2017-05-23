package io.skygear.skygear;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

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
     * The Roles.
     */
    Set<Role> roles;

    /**
     * The last login time.
     */
    Date lastLoginTime;

    /**
     * The last seen time.
     */
    Date lastSeenTime;

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
        this.roles = new TreeSet<>();
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

    /**
     * Gets last login time.
     *
     * @return the last login time
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * Gets last seen time.
     *
     * @return the last seen time
     */
    public Date getLastSeenTime() {
        return lastSeenTime;
    }

    /**
     * Get all user roles.
     *
     * @return the roles
     */
    public Role[] getRoles() {
        return this.roles.toArray(
                new Role[this.roles.size()]
        );
    }

    /**
     * Add a role.
     *
     * @param aRole the role
     */
    public void addRole(Role aRole) {
        this.roles.add(aRole);
    }

    /**
     * Remove a role.
     *
     * @param aRole the role
     */
    public void removeRole(Role aRole) {
        this.roles.remove(aRole);
    }

    /**
     * Check whether has a specific role.
     *
     * @param aRole the role
     * @return the boolean indicating whether the user has the specific role.
     */
    public boolean hasRole(Role aRole) {
        return this.roles.contains(aRole);
    }
}
