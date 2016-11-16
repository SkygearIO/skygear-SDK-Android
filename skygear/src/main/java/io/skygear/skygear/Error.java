package io.skygear.skygear;

/**
 * The Error for the response of Skygear Request.
 */
public class Error extends Exception {
    /**
     * Instantiates a new Error.
     *
     * @param detailMessage the detail message
     */
    public Error(String detailMessage) {
        super(detailMessage);
    }
}
