package io.skygear.skygear;


/**
 * The Response Handler for Saving Multiple Records
 */
public abstract class MultiRecordSaveResponseHandler
        extends RecordSaveResponseBaseHandler<Record[]>
{
    @Override
    protected final void onSaveRecordsSuccess(Record[] records) {
        this.onSaveSuccess(records);
    }
}
