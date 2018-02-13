package io.skygear.skygear;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import java.util.HashMap;

/**
 * The Disable Skygear User Request.
 */
public class SetDisableUserRequest extends Request {
    /**
     * Returns a new request to enable the specified user account.
     *
     * @param userID   the user id
     */
    public static SetDisableUserRequest enableUserRequest(@NonNull String userID)  {
        return new SetDisableUserRequest(userID, false, null, null);
    }

    /**
     * Returns a new request to disable the specified user account.
     *
     * @param userID   the user id
     */
    public static SetDisableUserRequest disableUserRequest(@NonNull String userID)  {
        return new SetDisableUserRequest(userID, true, null, null);
    }

    /**
     * Returns a new request to disable the specified user account.
     *
     * @param userID   the user id
     * @param message  the message to be shown to user
     * @param expiry   the date and time when the user is automatically enabled
     */
    public static SetDisableUserRequest disableUserRequest(@NonNull String userID, @Nullable String message, @Nullable Date expiry)  {
        return new SetDisableUserRequest(userID, true, message, expiry);
    }

    /**
     * Instantiates a new Set Disable User Request.
     *
     * @param userID   the user id
     * @param disabled whether the user is disabled
     * @param message  the message to be shown to user
     * @param expiry   the date and time when the user is automatically enabled
     */
    public SetDisableUserRequest(@NonNull String userID, Boolean disabled, @Nullable String message, @Nullable Date expiry) {
        super("auth:disable:set");
        this.data = new HashMap<>();
        this.data.put("auth_id", userID);
        this.data.put("disabled", disabled);
        if (message != null) {
            this.data.put("message", message);
        }
        if (expiry != null) {
            this.data.put("expiry", DateSerializer.stringFromDate(expiry));
        }
    }
}
