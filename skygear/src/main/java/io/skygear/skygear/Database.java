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

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

/**
 * The Skygear Database.
 * <p>
 * This class wraps the logic of public / private database concept in Skygear.
 * Also, all record CRUD should be achieved via this Class.
 * </p>
 */
public class Database {
    private static final String PUBLIC_DATABASE_NAME = "_public";
    private static final String PRIVATE_DATABASE_NAME = "_private";
    private static final List<String> AvailableDatabaseNames
            = Arrays.asList(PUBLIC_DATABASE_NAME, PRIVATE_DATABASE_NAME);

    private String name;
    private WeakReference<Container> containerRef;

    /**
     * Instantiates a new Database.
     * <p>
     * Please be reminded that the skygear container passed in would be weakly referenced.
     * </p>
     *
     * @param databaseName the database name
     * @param container    the container
     */
    public Database(String databaseName, Container container) {
        super();

        if (!Database.AvailableDatabaseNames.contains(databaseName)) {
            throw new InvalidParameterException("Invalid database name");
        }

        this.name = databaseName;
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
     * Gets the database name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Save a record.
     *
     * @param record  the record
     * @param handler the response handler
     */
    public void save(Record record, RecordSaveResponseHandler handler) {
        this.save(new Record[]{ record }, handler);
    }

    /**
     * Save multiple records.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void save(Record[] records, RecordSaveResponseHandler handler) {
        RecordSaveRequest request = new RecordSaveRequest(records, this);
        request.responseHandler = handler;

        this.getContainer().sendRequest(request);
    }

    /**
     * Query records.
     *
     * @param query   the query object
     * @param handler the response handler
     */
    public void query(Query query, RecordQueryResponseHandler handler) {
        RecordQueryRequest request = new RecordQueryRequest(query, this);
        request.responseHandler = handler;

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete a record.
     *
     * @param record  the record
     * @param handler the response handler
     */
    public void delete(Record record, RecordDeleteResponseHandler handler) {
        this.delete(new Record[] { record }, handler);
    }

    /**
     * Delete multiple records.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void delete(Record[] records, RecordDeleteResponseHandler handler) {
        RecordDeleteRequest request = new RecordDeleteRequest(records, this);
        request.responseHandler = handler;

        this.getContainer().sendRequest(request);
    }

    /**
     * Upload asset.
     *
     * @param asset           the asset
     * @param responseHandler the response handler
     */
    public void uploadAsset(
            final Asset asset,
            final AssetPostRequest.ResponseHandler responseHandler
    ) {
        final RequestManager requestManager = this.getContainer().requestManager;

        AssetPreparePostRequest preparePostRequest = new AssetPreparePostRequest(asset);
        preparePostRequest.responseHandler = new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                postRequest.responseHandler = responseHandler;
                requestManager.sendAssetPostRequest(postRequest);
            }

            @Override
            public void onPreparePostFail(Error error) {
                if (responseHandler != null) {
                    responseHandler.onPostFail(asset, error);
                }
            }
        };

        requestManager.sendRequest(preparePostRequest);
    }

    static class Factory {
        /**
         * Instantiates a public database.
         * <p>
         * Please be reminded that the skygear container passed in would be weakly referenced.
         * </p>
         *
         * @param container the skygear container
         * @return the database
         */
        static PublicDatabase publicDatabase(Container container) {
            return new PublicDatabase(PUBLIC_DATABASE_NAME, container);
        }

        /**
         * Instantiates a private database.
         * <p>
         * Please be reminded that the skygear container passed in would be weakly referenced.
         * </p>
         *
         * @param container the skygear container
         * @return the database
         */
        static Database privateDatabase(Container container) {
            return new Database(PRIVATE_DATABASE_NAME, container);
        }
    }
}
