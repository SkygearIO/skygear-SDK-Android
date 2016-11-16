package io.skygear.skygear;

/**
 * The Error for the response of Skygear Request.
 */
public class Error extends Exception {
    private final Code code;

    /**
     * Instantiates a new Error.
     * <p>
     *     This constructor will creates an Error with
     *     error code {@link Code#UNEXPECTED_ERROR}
     * </p>
     *
     * @param detailMessage the detail message
     */
    public Error(String detailMessage) {
        this(Code.UNEXPECTED_ERROR, detailMessage);
    }

    /**
     * Instantiates a new Error.
     * <p>
     *     This constructor will creates an Error and try to
     *     parse the error code value.
     * </p>
     *
     * @param codeValue     the code value
     * @param detailMessage the detail message
     */
    public Error(int codeValue, String detailMessage) {
        this(Code.fromValue(codeValue), detailMessage);
    }

    /**
     * Instantiates a new Error.
     *
     * @param code          the code
     * @param detailMessage the detail message
     */
    public Error(Code code, String detailMessage) {
        super(detailMessage);

        this.code = code;
    }

    /**
     * Gets error code.
     *
     * @return the code
     */
    public Code getCode() {
        return code;
    }

    /**
     * The Error Codes.
     */
    public static enum Code {
        /**
         * Not authenticated error.
         */
        NOT_AUTHENTICATED(101),
        /**
         * Permission denied error.
         */
        PERMISSION_DENIED(102),
        /**
         * Access key not accepted error.
         */
        ACCESS_KEY_NOT_ACCEPTED(103),
        /**
         * Access token not accepted error.
         */
        ACCESS_TOKEN_NOT_ACCEPTED(104),
        /**
         * Invalid credentials error.
         */
        INVALID_CREDENTIALS(105),
        /**
         * Invalid signature error.
         */
        INVALID_SIGNATURE(106),
        /**
         * Bad request error.
         */
        BAD_REQUEST(107),
        /**
         * Invalid argument error.
         */
        INVALID_ARGUMENT(108),
        /**
         * Duplicated error.
         */
        DUPLICATED(109),
        /**
         * Resource not found error.
         */
        RESOURCE_NOT_FOUND(110),
        /**
         * Not supported error.
         */
        NOT_SUPPORTED(111),
        /**
         * Not implemented error.
         */
        NOT_IMPLEMENTED(112),
        /**
         * Constraint violated error.
         */
        CONSTRAINT_VIOLATED(113),
        /**
         * Incompatible schema error.
         */
        INCOMPATIBLE_SCHEMA(114),
        /**
         * Atomic operation failure error.
         */
        ATOMIC_OPERATION_FAILURE(115),
        /**
         * Partial operation failure error.
         */
        PARTIAL_OPERATION_FAILURE(116),
        /**
         * Undefined operation error.
         */
        UNDEFINED_OPERATION(117),
        /**
         * Plugin unavailable error.
         */
        PLUGIN_UNAVAILABLE(118),
        /**
         * Plugin timeout error.
         */
        PLUGIN_TIMEOUT(119),
        /**
         * Record query invalid error.
         */
        RECORD_QUERY_INVALID(120),
        /**
         * Plugin initializing error.
         */
        PLUGIN_INITIALIZING(121),

        /**
         * Unexpected error.
         */
        UNEXPECTED_ERROR(10000);

        private final int value;

        private Code(int value) {
            this.value = value;
        }

        /**
         * Gets value of the error code.
         *
         * @return the value
         */
        public int getValue() {
            return this.value;
        }

        /**
         * Creates error code from value.
         *
         * @param value the value
         * @return the code
         */
        public static Code fromValue(int value) {
            Code[] codes = Code.values();
            for (Code eachCode : codes) {
                if (eachCode.getValue() == value) {
                    return eachCode;
                }
            }

            return Code.UNEXPECTED_ERROR;
        }
    }
}
