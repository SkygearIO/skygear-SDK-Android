package io.skygear.skygear;

import java.util.Arrays;

/**
 * The Skygear Record Fetch Request
 */
public class RecordFetchRequest extends RecordQueryRequest {

    public final String[] fetchingRecordIDs;
    public final String fetchingRecordType;

    /**
     * Instantiates a new Record Fetch Request.
     *
     * @param recordType the record type
     * @param recordIDs  the record i ds
     * @param database   the database
     */
    public RecordFetchRequest(String recordType, String[] recordIDs, Database database) {
        super(database);

        this.fetchingRecordType = recordType;
        this.fetchingRecordIDs = recordIDs;

        Query query = new Query(recordType);
        query.contains("_id", Arrays.asList(recordIDs));

        this.setQuery(query);
    }

    /**
     * Instantiates a new Record fetch request.
     *
     * @param recordType the record type
     * @param recordID   the record id
     * @param database   the database
     */
    public RecordFetchRequest(String recordType, String recordID, Database database) {
        this(recordType, new String[]{ recordID }, database);
    }
}
