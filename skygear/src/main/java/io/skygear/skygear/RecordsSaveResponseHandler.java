package io.skygear.skygear;


/**
 * The Response Handler for Saving Records
 */
public abstract class RecordsSaveResponseHandler
        extends RecordSaveResponseBaseHandler<Record[]>
{
    @Override
    protected final void onSaveRecordsSuccess(Record[] records) {
        this.onSaveSuccess(records);
    }
}
