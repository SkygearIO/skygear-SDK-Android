package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Skygear Asset Prepare Post Response Handler.
 */
public abstract class AssetPreparePostResponseHandler implements Request.ResponseHandler {

    private final Asset asset;

    /**
     * Instantiates a new Asset Prepare Post Response Handler.
     *
     * <p>
     *     The asset to be uploaded should be passed in since
     *     the name and url will be updated according to the
     *     response from Skygear Server
     * </p>
     *
     * @param asset the asset
     */
    public AssetPreparePostResponseHandler(Asset asset) {
        super();

        this.asset = asset;
    }

    /**
     * Prepare post success callback.
     *
     * @param postRequest the post request
     */
    public abstract void onPreparePostSuccess(AssetPostRequest postRequest);

    /**
     * Prepare post fail callback.
     *
     * @param reason the reason
     */
    public abstract void onPreparePostFail(String reason);

    @Override
    public void onSuccess(JSONObject result) {
        try {
            // parse asset return from server and update the asset object
            JSONObject assetObject = result.getJSONObject("asset");
            this.asset.name = assetObject.getString("$name");
            this.asset.url = assetObject.getString("$url");

            // parse post request object
            JSONObject postRequestObject = result.getJSONObject("post-request");
            String postRequestAction = postRequestObject.getString("action");
            Map<String, String> postExtraFields = null;
            if (!postRequestObject.isNull("extra-fields")) {
                postExtraFields = new HashMap<>();

                JSONObject extraFieldObject = postRequestObject.getJSONObject("extra-fields");
                Iterator<String> extraKeys = extraFieldObject.keys();
                while (extraKeys.hasNext()) {
                    String perKey = extraKeys.next();
                    String perValue = extraFieldObject.getString(perKey);

                    postExtraFields.put(perKey, perValue);
                }
            }

            AssetPostRequest postRequest = new AssetPostRequest(
                    this.asset,
                    postRequestAction,
                    postExtraFields
            );

            this.onPreparePostSuccess(postRequest);
        } catch (JSONException e) {
            this.onPreparePostFail("Malformed server response");
        }
    }

    @Override
    public void onFail(Request.Error error) {
        this.onPreparePostFail(error.getMessage());
    }
}
