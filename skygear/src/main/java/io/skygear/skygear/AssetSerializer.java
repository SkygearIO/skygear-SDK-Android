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
 * The Skygear Asset Serializer.
 *
 * This class converts between asset object and JSON object in Skygear defined format.
 */
public class AssetSerializer {

    /**
     * Serialize an asset
     *
     * @param asset the asset
     * @return the json object
     */
    public static JSONObject serialize(Asset asset) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$type", "asset");
            jsonObject.put("$name", asset.getName());
            jsonObject.put("$content_type", asset.getMimeType());

            if (asset.getUrl() != null) {
                jsonObject.put("$url", asset.getUrl());
            }

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize an asset from json object.
     *
     * @param assetJSONObject the asset json object
     * @return the asset
     * @throws JSONException the json exception
     */
    public static Asset deserialize(JSONObject assetJSONObject) throws JSONException {
        String typeValue = assetJSONObject.getString("$type");
        if (typeValue.equals("asset")) {
            String assetName = assetJSONObject.getString("$name");
            String assetUrl = assetJSONObject.getString("$url");
            String assetMimeType = assetJSONObject.getString("$content_type");

            return new Asset(assetName, assetUrl, assetMimeType);
        }

        throw new JSONException("Invalid $type value: " + typeValue);
    }

    /**
     * Determines whether an object is a JSON object in Skygear defined asset format.
     *
     * @param object the object
     * @return the indicating boolean
     */
    public static boolean isAssetFormat(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;
            return jsonObject.getString("$type").equals("asset") &&
                    !jsonObject.isNull("$name") &&
                    !jsonObject.isNull("$url");
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }
}