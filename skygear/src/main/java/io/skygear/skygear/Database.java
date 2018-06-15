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
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

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

    private static <T> List<T> findInObject(Object object, Class<T> klass) {
        if (klass.isInstance(object)) {
            List<T> wanted = new ArrayList<T>();
            wanted.add((T)object);
            return wanted;
        } else if (object instanceof Record) {
            return Database.findInObject(((Record)object).getData(), klass);
        } else if (object instanceof Map) {
            List<T> wanted = new ArrayList<T>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>)object).entrySet()) {
                wanted.addAll(Database.findInObject(entry.getValue(), klass));
            }
            return wanted;

        } else if (object instanceof List) {
            List<T> wanted = new ArrayList<T>();
            for (Object item : (List)object) {
                wanted.addAll(Database.findInObject(item, klass));
            }
            return wanted;
        } else if (object instanceof Object[]) {
            return Database.findInObject((List)object, klass);
        } else {
            return new ArrayList<T>();
        }
    }

    private static <T extends Object> Object replaceObject(Object object, Map<T, T> mapTable) {
        if (object == null) {
            return null;
        } else if (mapTable.containsKey(object)) {
            return mapTable.get(object);
        }

        if (object instanceof Record) {
            // TODO: It is better to create a clone of the Record object, but
            // the Record class does not offer a clone method.
            Record record = (Record)object;
            record.set((Map)Database.replaceObject(record.getData(), mapTable));
            return record;
        } else if (object instanceof Map) {
            Map<String, Object> newMap = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>)object).entrySet()) {
                newMap.put(entry.getKey(), Database.replaceObject(entry.getValue(), mapTable));
            }
            return newMap;
        } else if (object instanceof List) {
            List<Object> newList = new ArrayList<Object>();
            for (Object item : (List)object) {
                newList.add(Database.replaceObject(item, mapTable));
            }
            return newList;
        } else if (object instanceof Object[]) {
            return Database.replaceObject(Arrays.asList((Object[])object), mapTable);
        } else {
            return object;
        }
    }

    void presave(final Object object, final ResultCallback callback) { // package-private
        List<Asset> assetsToUpload = new ArrayList<Asset>();
        for (Asset asset : Database.findInObject(object, Asset.class)) {
            if (asset.isPendingUpload()) {
                assetsToUpload.add(asset);
            }
        }

        if (assetsToUpload.size() > 0) {
            this.uploadAssets(assetsToUpload, new ResultCallback<Map<Asset, Asset>>() {
                @Override
                public void onSuccess(Map<Asset, Asset> result) {
                    Object presavedObject = Database.replaceObject(object, result);
                    callback.onSuccess(presavedObject);
                }

                @Override
                public void onFailure(Error error) {
                    callback.onFailure(error);
                }
            });
        } else {
            callback.onSuccess(object);
        }
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
    public void save(final Record[] records, RecordSaveResponseHandler handler) {
        final Record[] recordsToSave = records;
        final RecordSaveResponseHandler responseHandler = handler;
        this.presave(records, new ResultCallback() {
            @Override
            public void onSuccess(Object result) {
                Record[] presavedRecords = null;
                if (result instanceof List) {
                    List<Record> theList = (List<Record>)result;
                    presavedRecords = theList.toArray(new Record[theList.size()]);
                } else if (result instanceof Record[]) {
                    presavedRecords = (Record[])result;
                } else {
                    presavedRecords = records;
                }
                RecordSaveRequest request = new RecordSaveRequest(presavedRecords, Database.this);
                request.responseHandler = responseHandler;

                Database.this.getContainer().sendRequest(request);
            }

            @Override
            public void onFailure(Error error) {
                responseHandler.onSaveFail(error);
            }
        });
    }

    /**
     * Save a record atomically.
     *
     * @param record  the record
     * @param handler the response handler
     */
    public void saveAtomically(Record record, RecordSaveResponseHandler handler) {
        this.saveAtomically(new Record[]{ record }, handler);
    }

    /**
     * Save multiple records atomically.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void saveAtomically(final Record[] records, RecordSaveResponseHandler handler) {
        final Record[] recordsToSave = records;
        final RecordSaveResponseHandler responseHandler = handler;
        this.presave(records, new ResultCallback() {
            @Override
            public void onSuccess(Object result) {
                Record[] presavedRecords = null;
                if (result instanceof List) {
                    List<Record> theList = (List<Record>)result;
                    presavedRecords = theList.toArray(new Record[theList.size()]);
                } else if (result instanceof Record[]) {
                    presavedRecords = (Record[])result;
                } else {
                    presavedRecords = records;
                }
                RecordSaveRequest request = new RecordSaveRequest(presavedRecords, Database.this);
                request.setAtomic(true);
                request.responseHandler = responseHandler;

                Database.this.getContainer().sendRequest(request);
            }

            @Override
            public void onFailure(Error error) {
                responseHandler.onSaveFail(error);
            }
        });
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

    private void uploadAssets(
            final List<Asset> assets,
            final ResultCallback<Map<Asset, Asset>> callback
    ) {
        final RequestManager requestManager = this.getContainer().requestManager;

        final Map<Asset, Asset> savedAssets = new HashMap<>();
        final List<Error> errors = new ArrayList<Error>();
        final List<Asset> allAssets = assets;
        final Semaphore lock = new Semaphore(1);

        for (final Asset asset : assets) {
            AssetPostRequest.ResponseHandler handler = new AssetPostRequest.ResponseHandler() {
                private void onAllFinished() {
                    if (errors.isEmpty()) {
                        callback.onSuccess(savedAssets);
                    } else {
                        callback.onFailure(errors.get(0));
                    }
                }

                @Override
                public void onPostSuccess(Asset savedAsset, String response) {
                    lock.acquireUninterruptibly();
                    savedAssets.put(asset, savedAsset);
                    boolean allFinished = allAssets.size() >= savedAssets.size() + errors.size();
                    lock.release();
                    if (allFinished) {
                        this.onAllFinished();
                    }
                }

                @Override
                public void onPostFail(Asset savedAsset, Error error) {
                    lock.acquireUninterruptibly();
                    errors.add(error);
                    boolean allFinished = allAssets.size() >= savedAssets.size() + errors.size();
                    lock.release();
                    if (allFinished) {
                        this.onAllFinished();
                    }
                }
            };

            this.uploadAsset(asset, handler);
        }
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
