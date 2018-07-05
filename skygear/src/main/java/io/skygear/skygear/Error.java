/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Error for the response of Skygear Request.
 */
public class Error extends Exception {
    private final int codeValue;
    private final String name;
    private final String detailMessage;
    private final JSONObject info;

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
        this(Code.UNEXPECTED_ERROR.getValue(), null, detailMessage, null);
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
        this(codeValue, null, detailMessage, null);
    }

    /**
     * Instantiates a new Error.
     *
     * @param codeValue     the code
     * @param name          the name of the error
     * @param detailMessage the detail message
     * @param info          the info from error message
     */
    public Error(int codeValue, String name, String detailMessage, JSONObject info) {
        super(Code.fromValue(codeValue).toString());

        this.codeValue = codeValue;
        this.name = name;
        this.detailMessage = detailMessage;
        this.info = info;
    }

    /**
     * Instantiates a new Error.
     *
     * @param jsonObject     the json error object
     * @throws JSONException if jsonObject does't contain error required attribute
     */
    public Error(JSONObject jsonObject) throws JSONException {
        this(jsonObject.getInt("code"), jsonObject.getString("name"), jsonObject.getString("message"), jsonObject.optJSONObject("info"));
    }

    /**
     * Gets error code.
     *
     * @return the code
     */
    public Code getCode() {
        return Code.fromValue(this.codeValue);
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
         * Response timeout error.
         */
        RESPONSE_TIMEOUT(122),
        /**
         * Denied argument error.
         */
        DENIED_ARGUMENT(123),
        /**
         * Record query denied error.
         */
        RECORD_QUERY_DENIED(124),
        /**
         * Not configured error.
         */
        NOT_CONFIGURED(125),
        /**
         * Password policy violated error.
         */
        PASSWORD_POLICY_VIOLATED(126),
        /**
         * User disabled error.
         */
        USER_DISABLED(127),
        /**
         * Verification required error.
         */
        VERIFICATION_REQUIRED(128),
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

        @Override
        public String toString() {
            switch (this) {
                case NOT_AUTHENTICATED:
                    return "You have to be authenticated to perform this operation.";
                case PERMISSION_DENIED:
                case ACCESS_KEY_NOT_ACCEPTED:
                case ACCESS_TOKEN_NOT_ACCEPTED:
                    return "You are not allowed to perform this operation.";
                case INVALID_CREDENTIALS:
                    return "You are not allowed to log in because the credentials you provided are not valid.";
                case INVALID_SIGNATURE:
                case BAD_REQUEST:
                    return "The server is unable to process the request.";
                case INVALID_ARGUMENT:
                    return "The server is unable to process the data.";
                case DUPLICATED:
                    return "This request contains duplicate of an existing resource on the server.";
                case RESOURCE_NOT_FOUND:
                    return "The requested resource is not found.";
                case NOT_SUPPORTED:
                    return "This operation is not supported.";
                case NOT_IMPLEMENTED:
                    return "This operation is not implemented.";
                case CONSTRAINT_VIOLATED:
                case INCOMPATIBLE_SCHEMA:
                case ATOMIC_OPERATION_FAILURE:
                case PARTIAL_OPERATION_FAILURE:
                    return "A problem occurred while processing this request.";
                case UNDEFINED_OPERATION:
                    return "The requested operation is not available.";
                case PLUGIN_INITIALIZING:
                case PLUGIN_UNAVAILABLE:
                    return "The server is not ready yet.";
                case PLUGIN_TIMEOUT:
                    return "The server took too long to process.";
                case RECORD_QUERY_INVALID:
                    return "The server is unable to process the query.";
                case RESPONSE_TIMEOUT:
                    return "The server timed out while processing the request.";
                case DENIED_ARGUMENT:
                    return "The server is unable to process the data.";
                case RECORD_QUERY_DENIED:
                    return "You are not allowed to perform this operation.";
                case NOT_CONFIGURED:
                    return "The server is not configured for this operation.";
                case PASSWORD_POLICY_VIOLATED:
                    return "The password does not meet policy requirement.";
                case USER_DISABLED:
                    return "The user is disabled.";
                case UNEXPECTED_ERROR:
                    return "An unexpected error has occurred.";
            }
            return super.toString();
        }
    }

    public String getName () {
        return this.name;
    }
    public int getCodeValue() {
        return this.codeValue;
    }

    public String getDetailMessage() {
        return this.detailMessage;
    }

    public JSONObject getInfo() {
        return this.info;
    }
}
