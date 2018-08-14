package io.skygear.skygear;

/**
 * The Response Handler for Fetching Skygear Record
 */
public abstract class RecordFetchResponseHandler extends RecordFetchResponseBaseHandler<Record> {
    @Override
    protected void onHandleFetchResults(RecordResult<Record>[] results) {
        if (results == null || results.length == 0) {
            this.onFetchError(new Error("Cannot find any saved records"));
            return;
        }

        RecordResult<Record> firstResult = results[0];
        if (firstResult.isError()) {
            this.onFetchError(firstResult.error);
            return;
        }

        this.onFetchSuccess(firstResult.value);
    }
}
