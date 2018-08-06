package io.skygear.skygear;

/**
 * Base Abstract Class for The Record Delete Response Handler
 * @param <T> The Result Type
 */
public abstract class RecordDeleteResponseBaseHandler<T> extends ResponseHandler {
    /**
     * Delete success callback.
     *
     * @param result the deleted record result
     */
    public abstract void onDeleteSuccess(T result);

    /**
     * Delete fail callback.
     *
     * @param error the error
     */
    public abstract void onDeleteFail(Error error);

    @Override
    public final void onFail(Error error) {
        this.onDeleteFail(error);
    }
}
