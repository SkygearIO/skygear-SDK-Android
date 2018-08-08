package io.skygear.skygear;

import java.util.HashMap;
import java.util.Map;

/**
 * The Base Class for Fetching Record Response Handler
 */
public abstract class RecordFetchResponseBaseHandler<T> extends RecordQueryResponseHandler {

    public abstract void onFetchSuccess(T result);

    public abstract void onFetchError(Error error);

    protected abstract void onHandleFetchResults(RecordResult<Record>[] results);

    @Override
    public final void onQuerySuccess(Record[] records) {
        super.onQuerySuccess(records);

        Map<String, Record> recordMap = new HashMap<>();
        for (Record eachRecord : records) {
            recordMap.put(eachRecord.getId(), eachRecord);
        }

        Request request = this.getRequest();
        if (request == null || !(request instanceof RecordFetchRequest)) {
            this.onFetchError(new Error("Cannot find the request from response handler"));
        }

        RecordFetchRequest fetchRequest = (RecordFetchRequest)request;

        String fetchingRecordType = fetchRequest.fetchingRecordType;
        String[] fetchingRecordIDs = fetchRequest.fetchingRecordIDs;

        RecordResult<Record>[] results = new RecordResult[fetchingRecordIDs.length];

        for (int idx = 0; idx < fetchingRecordIDs.length; idx++) {
            String eachFetchingRecordID = fetchingRecordIDs[idx];
            if (!recordMap.containsKey(eachFetchingRecordID)) {
                Error eachRecordNotFoundError = new Error(
                        Error.Code.RESOURCE_NOT_FOUND.getValue(),
                        String.format(
                                "Cannot find %s record with ID %s",
                                fetchingRecordType,
                                eachFetchingRecordID
                        )
                );
                results[idx] = new RecordResult<>(null, eachRecordNotFoundError);
            } else {
                results[idx] = new RecordResult<>(recordMap.get(eachFetchingRecordID));
            }
        }

        this.onHandleFetchResults(results);
    }

    @Override
    public final void onQuerySuccess(Record[] records, QueryInfo queryInfo) {
        super.onQuerySuccess(records, queryInfo);
    }

    @Override
    public final void onQueryError(Error error) {
        this.onFetchError(error);
    }
}
