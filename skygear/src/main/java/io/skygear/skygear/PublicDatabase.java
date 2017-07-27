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
}
