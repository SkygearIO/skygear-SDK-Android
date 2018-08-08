package io.skygear.skygear;

/**
 * The Response Handler for Save Record
 */
public abstract class RecordSaveResponseHandler
        extends RecordSaveResponseBaseHandler<Record>
{
    @Override
    protected final void onSaveRecordsSuccess(Record[] records) {
        if (records == null || records.length == 0) {
            this.onSaveFail(new Error("Cannot find any saved records"));
            return;
        }

        this.onSaveSuccess(records[0]);
    }
}
