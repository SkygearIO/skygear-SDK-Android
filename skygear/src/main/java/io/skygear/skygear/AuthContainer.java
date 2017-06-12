package io.skygear.skygear;


import android.util.Log;

/**
 * Auth Container for Skygear.
 */
public class AuthContainer implements AuthResolver {
    private static final String TAG = "Skygear SDK";

    private final Container container;
    private final RequestManager requestManager;
    private final PersistentStore persistentStore;

    AuthContainer(Container container) {
        this.container = container;
        this.requestManager = container.requestManager;
        this.persistentStore = container.persistentStore;
    }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public User getCurrentUser() {
        return this.container.persistentStore.currentUser;
    }

    /**
     * Sign up with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithUsername(String username, String password, AuthResponseHandler handler) {
        Request req = new SignupRequest(username, null, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Sign up with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithEmail(String email, String password, AuthResponseHandler handler) {
        Request req = new SignupRequest(null, email, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Sign up anonymously.
     *
     * @param handler the handler
     */
    public void signupAnonymously(AuthResponseHandler handler) {
        Request req = new SignupRequest();
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Login with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithUsername(String username, String password, AuthResponseHandler handler) {
        Request req = new LoginRequest(username, null, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Login with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithEmail(String email, String password, AuthResponseHandler handler) {
        Request req = new LoginRequest(null, email, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Logout.
     *
     * @param handler the response handler
     */
    public void logout(LogoutResponseHandler handler) {
        final Request logoutRequest = new LogoutRequest();
        logoutRequest.responseHandler = new LogoutResponseHandlerWrapper(this, handler);

        String deviceId = this.persistentStore.deviceId;
        if (this.getCurrentUser() != null && deviceId != null) {
            // Try to unregister the device token before login out
            this.container.unregisterDeviceToken(new UnregisterDeviceResponseHandler() {
                @Override
                public void onUnregisterSuccess(String deviceId) {
                    AuthContainer.this.requestManager.sendRequest(logoutRequest);
                }

                @Override
                public void onUnregisterError(Error error) {
                    Log.w(TAG, "Fail to unregister device", error);
                    AuthContainer.this.requestManager.sendRequest(logoutRequest);
                }
            });
        } else {
            this.requestManager.sendRequest(logoutRequest);
        }
    }

    /**
     * Asks "Who Am I?"
     *
     * This API gets current user from server using current access token.
     *
     * @param handler the handler
     */
    public void whoami(AuthResponseHandler handler) {
        Request req = new GetCurrentUserRequest();
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.requestManager.sendRequest(req);
    }

    /**
     * Gets user by email.
     *
     * @param email   the email
     * @param handler the response handler
     */
    public void getUserByEmail(String email, UserQueryResponseHandler handler) {
        this.getUserByEmails(new String[] { email }, handler);
    }

    /**
     * Gets user by emails.
     *
     * @param emails  the emails
     * @param handler the response handler
     */
    public void getUserByEmails(String[] emails, UserQueryResponseHandler handler) {
        UserQueryByEmailsRequest request = new UserQueryByEmailsRequest(emails);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Gets user by username.
     *
     * @param username   the username
     * @param handler the response handler
     */
    public void getUserByUsername(String username, UserQueryResponseHandler handler) {
        this.getUserByUsernames(new String[] { username }, handler);
    }

    /**
     * Gets users by usernames.
     *
     * @param usernames  the usernames
     * @param handler the response handler
     */
    public void getUserByUsernames(String[] usernames, UserQueryResponseHandler handler) {
        UserQueryByUsernamesRequest request = new UserQueryByUsernamesRequest(usernames);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    /**
     * Save user.
     *
     * @param user    the user
     * @param handler the response handler
     */
    public void saveUser(User user, UserSaveResponseHandler handler) {
        UserSaveRequest request = new UserSaveRequest(user);
        request.responseHandler = handler;

        this.requestManager.sendRequest(request);
    }

    @Override
    public void resolveAuthUser(User user) {
        this.persistentStore.currentUser = user;
        this.persistentStore.save();

        this.requestManager.accessToken = user != null ? user.accessToken : null;
        this.container.registerDeviceToken(this.persistentStore.deviceToken);
    }

}
