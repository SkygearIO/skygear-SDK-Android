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


import android.util.Log;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Auth Container for Skygear.
 */
public class AuthContainer implements AuthResolver {
    private static final String TAG = "Skygear SDK";

    private WeakReference<Container> containerRef;

    public AuthContainer(Container container) {
        this.containerRef = new WeakReference<>(container);
    }

    /**
     * Gets container.
     *
     * @return the container
     */
    public Container getContainer() {
        Container container = this.containerRef.get();
        if (container == null) {
            throw new InvalidParameterException("Missing container for database");
        }

        return container;
    }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public Record getCurrentUser() {
        return this.getContainer().persistentStore.currentUser;
    }

    /**
     * Gets current access token
     *
     * @return the current access token
     */
    public String getCurrentAccessToken() {
        return this.getContainer().persistentStore.accessToken;
    }

    /**
     * Sign up with auth data.
     *
     * @param authData the unique identifier of a user
     * @param password the password
     * @param handler  the response handler
     */
    public void signup(Map<String, Object> authData, String password, AuthResponseHandler handler) {
        this.signup(authData, password, null, handler);
    }

    /**
     * Sign up with auth data.
     *
     * @param authData the unique identifier of a user
     * @param password the password
     * @param profile  the user profile
     * @param handler  the response handler
     */
    public void signup(Map<String, Object> authData, String password, Map<String, Object> profile, AuthResponseHandler handler) {
        Request req = new SignupRequest(authData, password, profile);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.getContainer().requestManager.sendRequest(req);
    }

    /**
     * Sign up with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithUsername(String username, String password, AuthResponseHandler handler) {
        this.signupWithUsername(username, password, null, handler);
    }

    /**
     * Sign up with username.
     *
     * @param username the username
     * @param password the password
     * @param profile  the user profile
     * @param handler  the response handler
     */
    public void signupWithUsername(String username, String password, Map<String, Object> profile, AuthResponseHandler handler) {
        Map<String, Object> authData = new HashMap<>();
        authData.put("username", username);

        this.signup(authData, password, profile, handler);
    }

    /**
     * Sign up with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void signupWithEmail(String email, String password, AuthResponseHandler handler) {
        this.signupWithEmail(email, password, null, handler);
    }

    /**
     * Sign up with email.
     *
     * @param email    the email
     * @param password the password
     * @param profile  the user profile
     * @param handler  the response handler
     */
    public void signupWithEmail(String email, String password, Map<String, Object> profile, AuthResponseHandler handler) {
        Map<String, Object> authData = new HashMap<>();
        authData.put("email", email);

        this.signup(authData, password, profile, handler);
    }

    /**
     * Sign up anonymously.
     *
     * @param handler the handler
     */
    public void signupAnonymously(AuthResponseHandler handler) {
        Request req = new SignupRequest();
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.getContainer().requestManager.sendRequest(req);
    }

    /**
     * Login with auth data.
     *
     * @param authData the unique identifier of a user
     * @param password the password
     * @param handler  the response handler
     */
    public void login(Map<String, Object> authData, String password, AuthResponseHandler handler) {
        Request req = new LoginRequest(authData, password);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.getContainer().requestManager.sendRequest(req);
    }

    /**
     * Login with username.
     *
     * @param username the username
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithUsername(String username, String password, AuthResponseHandler handler) {
        Map<String, Object> authData = new HashMap<>();
        authData.put("username", username);

        this.login(authData, password, handler);
    }

    /**
     * Login with email.
     *
     * @param email    the email
     * @param password the password
     * @param handler  the response handler
     */
    public void loginWithEmail(String email, String password, AuthResponseHandler handler) {
        Map<String, Object> authData = new HashMap<>();
        authData.put("email", email);

        this.login(authData, password, handler);
    }

    /**
     * Logout.
     *
     * @param handler the response handler
     */
    public void logout(LogoutResponseHandler handler) {
        final Request logoutRequest = new LogoutRequest();
        logoutRequest.responseHandler = new LogoutResponseHandlerWrapper(this, handler);

        String deviceId = this.getContainer().persistentStore.deviceId;
        if (this.getCurrentUser() != null && deviceId != null) {
            // Try to unregister the device token before login out
            this.getContainer().push.unregisterDeviceToken(new UnregisterDeviceResponseHandler() {
                @Override
                public void onUnregisterSuccess(String deviceId) {
                    AuthContainer.this.getContainer().requestManager.sendRequest(logoutRequest);
                }

                @Override
                public void onUnregisterError(Error error) {
                    Log.w(TAG, "Fail to unregister device", error);
                    AuthContainer.this.getContainer().requestManager.sendRequest(logoutRequest);
                }
            });
        } else {
            this.getContainer().requestManager.sendRequest(logoutRequest);
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

        this.getContainer().requestManager.sendRequest(req);
    }

    /**
     * Call forgot password lambda function.
     *
     * @param email   the email which the forgot password email should be sent to
     * @param handler the response handler
     */
    public void forgotPassword(String email, LambdaResponseHandler handler) {
        Object[] argv = new Object[]{email};
        this.getContainer().callLambdaFunction("user:forgot-password", argv, handler);
    }

    /**
     * Change user password
     *
     * @param newPassword the new password
     * @param oldPassword the old password
     * @param handler     the response handler
     */
    public void changePassword(String newPassword, String oldPassword, AuthResponseHandler handler) {
        Request req = new ChangePasswordRequest(newPassword, oldPassword);
        req.responseHandler = new AuthResponseHandlerWrapper(this, handler);

        this.getContainer().requestManager.sendRequest(req);
    }

    /**
     * Call reset password lambda function.
     *
     * @param userID      the user whose is resetting password
     * @param code        the code user received for forgot password
     * @param expireAt    when should the reset password url expire
     * @param newPassword the new password after resetting
     * @param handler the response handler
     */
    public void resetPassword(String userID, String code, Date expireAt, String newPassword, LambdaResponseHandler handler) {
        Object[] argv = new Object[]{userID, code, expireAt, newPassword};
        this.getContainer().callLambdaFunction("user:reset-password", argv, handler);
    }

    /**
     * Sets admin roles.
     *
     * @param role    the roles array
     * @param handler the handler
     */
    public void setAdminRole(Role[] role, SetRoleResponseHandler handler) {
        SetAdminRoleRequest request = new SetAdminRoleRequest(role);
        request.responseHandler = handler;

        RequestManager requestManager = this.getContainer().requestManager;
        requestManager.sendRequest(request);
    }

    /**
     * Sets an admin role.
     *
     * @param role    the role
     * @param handler the handler
     */
    public void setAdminRole(Role role, SetRoleResponseHandler handler) {
        this.setAdminRole(new Role[] { role }, handler);
    }

    /**
     * Sets default roles.
     *
     * @param role    the role array
     * @param handler the handler
     */
    public void setDefaultRole(Role[] role, SetRoleResponseHandler handler) {
        SetDefaultRoleRequest request = new SetDefaultRoleRequest(role);
        request.responseHandler = handler;

        RequestManager requestManager = this.getContainer().requestManager;
        requestManager.sendRequest(request);
    }

    /**
     * Sets a default role.
     *
     * @param role    the role
     * @param handler the handler
     */
    public void setDefaultRole(Role role, SetRoleResponseHandler handler) {
        this.setDefaultRole(new Role[] { role }, handler);
    }

    /**
     * Get user role
     *
     * @param users   the users array
     * @param handler the handler
     */
    public void fetchUserRole(Record[] users, FetchUserRoleResponseHandler handler) {
        this.fetchUserRole(this.getUserIDs(users), handler);
    }

    /**
     * Get user role
     *
     * @param userIDs the user id array
     * @param handler the handler
     */
    public void fetchUserRole(String[] userIDs, FetchUserRoleResponseHandler handler) {
        FetchUserRoleRequest request = new FetchUserRoleRequest(userIDs);
        request.responseHandler = handler;

        RequestManager requestManager = this.getContainer().requestManager;
        requestManager.sendRequest(request);
    }

    /**
     * Assign user role
     *
     * @param users   the users array
     * @param roles   the roles array
     * @param handler the handler
     */
    public void assignUserRole(Record[] users, Role[] roles, SetUserRoleResponseHandler handler) {
        this.assignUserRole(this.getUserIDs(users), this.getRoleNames(roles), handler);
    }

    /**
     * Assign user role
     *
     * @param userIDs   the user id array
     * @param roleNames the role name array
     * @param handler   the handler
     */
    public void assignUserRole(String[] userIDs, String[] roleNames, SetUserRoleResponseHandler handler) {
        AssignUserRoleRequest request = new AssignUserRoleRequest(userIDs, roleNames);
        request.responseHandler = handler;

        RequestManager requestManager = this.getContainer().requestManager;
        requestManager.sendRequest(request);
    }

    /**
     * Assign user role
     *
     * @param users   the users array
     * @param roles   the roles array
     * @param handler the handler
     */
    public void revokeUserRole(Record[] users, Role[] roles, SetUserRoleResponseHandler handler) {
        this.revokeUserRole(this.getUserIDs(users), this.getRoleNames(roles), handler);
    }

    /**
     * Revoke user role
     *
     * @param userIDs   the user id array
     * @param roleNames the role name array
     * @param handler   the handler
     */
    public void revokeUserRole(String[] userIDs, String[] roleNames, SetUserRoleResponseHandler handler) {
        RevokeUserRoleRequest request = new RevokeUserRoleRequest(userIDs, roleNames);
        request.responseHandler = handler;

        RequestManager requestManager = this.getContainer().requestManager;
        requestManager.sendRequest(request);
    }

    private String[] getUserIDs(Record[] users) {
        String[] userIDs = new String[users.length];
        for (int i = 0; i < users.length; i++) {
            Record user = users[i];
            if (!user.getType().equals("user")) {
                throw new InvalidParameterException("Record type should be user");
            }

            userIDs[i] = users[i].getId();
        }

        return userIDs;
    }

    private String[] getRoleNames(Role[] roles) {
        String[] roleNames = new String[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roleNames[i] = roles[i].getName();
        }

        return roleNames;
    }

    @Override
    public void resolveAuthUser(Record user, String accessToken) {
        Container container = this.getContainer();
        container.persistentStore.currentUser = user;
        container.persistentStore.accessToken = accessToken;
        container.persistentStore.save();

        container.requestManager.accessToken = accessToken;
        container.push.registerDeviceToken(container.persistentStore.deviceToken);
    }

}
