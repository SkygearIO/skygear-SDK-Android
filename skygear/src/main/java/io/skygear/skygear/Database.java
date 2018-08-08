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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

            for (Object item : ((Map)object).values()) {
                wanted.addAll(Database.findInObject(item, klass));
            }
            return wanted;

        } else if (object instanceof List) {
            List<T> wanted = new ArrayList<T>();
            for (Object item : (List)object) {
                wanted.addAll(Database.findInObject(item, klass));
            }
            return wanted;
        } else if (object instanceof Object[]) {
            List<T> wanted = new ArrayList<T>();
            for (Object item : (Object[])object) {
                wanted.addAll(Database.findInObject(item, klass));
            }
            return wanted;
        } else {
            return new ArrayList<T>();
        }
    }

    private static Object replaceObject(Object object, Map mapTable) {
        if (object == null) {
            return null;
        } else if (mapTable.containsKey(object)) {
            return mapTable.get(object);
        }

        if (object instanceof Record) {
            return Database.replaceObject((Record)object, mapTable);
        } else if (object instanceof Map) {
            return Database.replaceObject((Map)object, mapTable);
        } else if (object instanceof List) {
            return Database.replaceObject((List)object, mapTable);
        } else if (object instanceof Object[]) {
            return Database.replaceObject((Object[])object, mapTable);
        } else {
            return object;
        }
    }

    private static Map replaceObject(Map object, Map mapTable) {
        Map newMap = new HashMap();
        Iterator it = object.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            newMap.put(entry.getKey(), Database.replaceObject(entry.getValue(), mapTable));
        }
        return newMap;
    }

    private static List replaceObject(List object, Map mapTable) {
        List newList = new ArrayList();
        for (Object item : object) {
            newList.add(Database.replaceObject(item, mapTable));
        }
        return newList;
    }

    private static Object[] replaceObject(Object[] object, Map mapTable) {
        Object[] newArray = new Object[object.length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = Database.replaceObject(object[i], mapTable);
        }
        return newArray;
    }

    private static Record replaceObject(Record object, Map mapTable) {
        // TODO: It is better to create a clone of the Record object, but
        // the Record class does not offer a clone method.
        Record record = (Record)object;
        record.set((Map)Database.replaceObject(record.getData(), mapTable));
        return record;
    }

    private void presaveAssets(final Object object, final ResultHandling<Map<Asset, Asset>> callback) {
        List<Asset> assetsToUpload = new ArrayList<Asset>();
        for (Asset asset : Database.findInObject(object, Asset.class)) {
            if (asset.isPendingUpload()) {
                assetsToUpload.add(asset);
            }
        }

        if (assetsToUpload.size() > 0) {
            this.uploadAssets(assetsToUpload, callback);
        } else {
            callback.onSuccess(new HashMap<Asset, Asset>());
        }
    }

    private void presave(final Record[] object, final ResultHandling<Record[]> callback) {
        this.presaveAssets(object, new ResultHandling<Map<Asset, Asset>>() {
            @Override
            public final void onSuccess(Map<Asset, Asset> result) {
                Record[] newArray = new Record[object.length];
                for (int i = 0; i < object.length; i++) {
                    newArray[i] = Database.replaceObject(object[i], result);
                }
                callback.onSuccess(newArray);
            }

            @Override
            public final void onFailure(Error error) {
                callback.onFailure(error);
            }
        });
    }

    void presave(final List object, final ResultHandling<List> callback) { // package-private
        this.presaveAssets(object, new ResultHandling<Map<Asset, Asset>>() {
            @Override
            public final void onSuccess(Map<Asset, Asset> result) {
                callback.onSuccess(Database.replaceObject(object, result));
            }

            @Override
            public final void onFailure(Error error) {
                callback.onFailure(error);
            }
        });
    }

    void presave(final Map object, final ResultHandling<Map> callback) { // package-private
        this.presaveAssets(object, new ResultHandling<Map<Asset, Asset>>() {
            @Override
            public final void onSuccess(Map<Asset, Asset> result) {
                callback.onSuccess(Database.replaceObject(object, result));
            }

            @Override
            public final void onFailure(Error error) {
                callback.onFailure(error);
            }
        });
    }

    /**
     * Save a record.
     *
     * @param record  the record
     * @param handler the response handler
     */
    public void save(Record record, RecordSaveResponseLegacyHandler handler) {
        this.save(new Record[]{ record }, handler);
    }

    /**
     * Save multiple records.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void save(final Record[] records, RecordSaveResponseLegacyHandler handler) {
        final Record[] recordsToSave = records;
        final RecordSaveResponseLegacyHandler responseHandler = handler;
        this.presave(records, new ResultHandling<Record[]>() {
            @Override
            public final void onSuccess(Record[] result) {
                RecordSaveRequest request = new RecordSaveRequest(result, Database.this);
                request.setResponseHandler(responseHandler);

                Database.this.getContainer().sendRequest(request);
            }

            @Override
            public final void onFailure(Error error) {
                responseHandler.onSaveFail(error);
            }
        });
    }

    /**
     * Save multiple records atomically.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void saveAtomically(final Record[] records, RecordSaveResponseLegacyHandler handler) {
        final Record[] recordsToSave = records;
        final RecordSaveResponseLegacyHandler responseHandler = handler;
        this.presave(records, new ResultHandling<Record[]>() {
            @Override
            public final void onSuccess(Record[] result) {
                RecordSaveRequest request = new RecordSaveRequest(result, Database.this);
                request.setAtomic(true);
                request.setResponseHandler(responseHandler);

                Database.this.getContainer().sendRequest(request);
            }

            @Override
            public final void onFailure(Error error) {
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
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete a record.
     *
     * @param record  the record
     * @param handler the response handler
     */
    public void delete(Record record, RecordDeleteResponseHandler handler) {
        RecordDeleteRequest request = new RecordDeleteRequest(new Record[]{record}, this);
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete multiple records.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void delete(Record[] records, MultiRecordDeleteResponseHandler handler) {
        RecordDeleteRequest request = new RecordDeleteRequest(records, this);
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete a record.
     *
     * @param recordType the record type
     * @param recordID   the record ID
     * @param handler    the response handler
     */
    public void delete(String recordType, String recordID, RecordDeleteByIDResponseHandler handler) {
        RecordDeleteRequest request = new RecordDeleteRequest(recordType, new String[]{recordID}, this);
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete multiple records.
     *
     * @param recordType the record type
     * @param recordIDs  the record IDs
     * @param handler    the response handler
     */
    public void delete(String recordType, String[] recordIDs, MultiRecordDeleteByIDResponseHandler handler) {
        RecordDeleteRequest request = new RecordDeleteRequest(recordType, recordIDs, this);
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete records by IDs non-atomically.
     *
     * @param recordType the record type
     * @param recordIDs  the record IDs
     * @param handler    the response handler
     */
    public void deleteNonAtomically(
            String recordType,
            String[] recordIDs,
            RecordNonAtomicDeleteByIDResponseHandler handler)
    {
        RecordDeleteRequest request = new RecordDeleteRequest(recordType, recordIDs, this);
        request.setAtomic(false);
        request.setResponseHandler(handler);

        this.getContainer().sendRequest(request);
    }

    /**
     * Delete records non-atomically.
     *
     * @param records the records
     * @param handler the response handler
     */
    public void deleteNonAtomically(
            Record[] records,
            RecordNonAtomicDeleteResponseHandler handler
    ) {
        RecordDeleteRequest request = new RecordDeleteRequest(records, this);
        request.setAtomic(false);
        request.setResponseHandler(handler);

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
        preparePostRequest.setResponseHandler(new AssetPreparePostResponseHandler(asset) {
            @Override
            public void onPreparePostSuccess(AssetPostRequest postRequest) {
                postRequest.setResponseHandler(responseHandler);
                requestManager.sendAssetPostRequest(postRequest);
            }

            @Override
            public void onPreparePostFail(Error error) {
                if (responseHandler != null) {
                    responseHandler.onPostFail(asset, error);
                }
            }
        });

        requestManager.sendRequest(preparePostRequest);
    }

    private void uploadAssets(
            final List<Asset> assets,
            final ResultHandling<Map<Asset, Asset>> callback
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
