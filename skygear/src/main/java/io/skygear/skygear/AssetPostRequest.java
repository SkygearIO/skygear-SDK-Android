package io.skygear.skygear;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Skygear Asset Post Request.
 */
public class AssetPostRequest implements Response.Listener<String>, Response.ErrorListener {
    /**
     * The Asset.
     */
    Asset asset;

    /**
     * The Post Action.
     */
    String action;
    /**
     * The Extra Fields of the Post Request.
     */
    Map<String, String> extraFields;

    /**
     * The Response Handler.
     */
    public ResponseHandler responseHandler;

    /**
     * Instantiates a new Skygear Asset Post Request.
     *
     * @param asset       the asset
     * @param action      the action
     * @param extraFields the extra fields
     */
    public AssetPostRequest(Asset asset, String action, Map<String, String> extraFields) {
        super();

        this.asset = asset;
        this.action = action;
        this.extraFields = new HashMap<>();

        if (extraFields != null) {
            this.extraFields.putAll(extraFields);
        }
    }

    /**
     * Gets the asset.
     *
     * @return the asset
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Gets the post action.
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Gets the extra fields.
     *
     * @return the extra fields
     */
    public Map<String, String> getExtraFields() {
        return extraFields;
    }

    /**
     * Validate method.
     * This is be called before sending out the request.
     * Throw an exception to indicate any validation error.
     *
     * @throws Exception the exception
     */
    protected void validate() throws Exception {
        if (this.getAsset().getSize() <= 0) {
            throw new InvalidParameterException("Missing data for the asset");
        }
    }

    /**
     * Validation error callback
     *
     * @param exception the exception
     */
    public void onValidationError(Exception exception) {
        if (this.responseHandler != null) {
            this.responseHandler.onPostFail(this.asset, exception.getMessage());
        }
    }

    @Override
    public void onResponse(String response) {
        this.asset.pendingUpload = false;
        if (this.responseHandler != null) {
            this.responseHandler.onPostSuccess(this.asset, response);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (this.responseHandler != null) {
            this.responseHandler.onPostFail(this.asset, error.getMessage());
        }
    }

    /**
     * The Skygear Asset Post Response Handler interface.
     */
    public interface ResponseHandler {
        /**
         * Post success callback.
         *
         * @param asset    the asset
         * @param response the response
         */
        void onPostSuccess(Asset asset, String response);

        /**
         * Post fail callback.
         *
         * @param asset  the asset
         * @param reason the reason
         */
        void onPostFail(Asset asset, String reason);
    }
}

