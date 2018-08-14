package io.skygear.skygear;

/**
 * The Result of Record Operation
 *
 * @param <T> the value type of the result
 */
public class RecordResult<T> {
    public final T value;
    public final Error error;

    public RecordResult(T value) {
        this(value, null);
    }

    public RecordResult(T value, Error error) {
        this.value = value;
        this.error = error;
    }

    public boolean isError() {
        return this.error != null;
    }
}
