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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The Skygear Asset Prepare Post Response Handler.
 */
public abstract class AssetPreparePostResponseHandler implements ResponseHandler {

    private final Asset asset;

    /**
     * Instantiates a new Asset Prepare Post Response Handler.
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
     * @param error the error
     */
    public abstract void onPreparePostFail(Error error);

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
            this.onPreparePostFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        this.onPreparePostFail(error);
    }
}
