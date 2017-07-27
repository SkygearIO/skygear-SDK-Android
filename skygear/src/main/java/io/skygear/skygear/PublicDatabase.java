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


import java.security.InvalidParameterException;

/**
 * The Skygear Public Database.
 * <p>
 * This class wraps the logic of public database concept in Skygear.
 * In adition to record CRUD, this class also wraps record access api.
 * </p>
 */
public class PublicDatabase extends Database {
    /**
     * Instantiates a new Database.
     * <p>
     * Please be reminded that the skygear container passed in would be weakly referenced.
     * </p>
     *
     * @param databaseName the database name
     * @param container    the container
     */
    public PublicDatabase(String databaseName, Container container) {
        super(databaseName, container);
    }

    /**
     * Gets default access control.
     *
     * @return the access control
     */
    public AccessControl getDefaultAccessControl() {
        return AccessControl.defaultAccessControl();
    }

    /**
     * Sets default access control.
     *
     * @param accessControl the access control
     */
    public void setDefaultAccessControl(AccessControl accessControl) {
        PersistentStore persistentStore = this.getContainer().persistentStore;
        persistentStore.defaultAccessControl = accessControl;
        persistentStore.save();

        AccessControl.defaultAccessControl = accessControl;
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
}
