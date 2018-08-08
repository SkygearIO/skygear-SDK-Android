package io.skygear.skygear;

/**
 * The Response Handler for Fetching Skygear Records
 */
public abstract class MultiRecordFetchResponseHandler
        extends RecordFetchResponseBaseHandler<RecordResult<Record>[]>
{
    @Override
    protected void onHandleFetchResults(RecordResult<Record>[] results) {
        if (results == null || results.length == 0) {
            this.onFetchError(new Error("Cannot find any saved records"));
            return;
        }

        this.onFetchSuccess(results);
    }
}
