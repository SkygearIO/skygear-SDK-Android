package io.skygear.skygear;

/**
 * The Skygear Authentication exception.
 */
public class AuthenticationException extends Exception {
    /**
     * Instantiates a new Authentication exception.
     *
     * @param detailMessage the detail message
     */
    public AuthenticationException(String detailMessage) {
        super(detailMessage);
    }
}
