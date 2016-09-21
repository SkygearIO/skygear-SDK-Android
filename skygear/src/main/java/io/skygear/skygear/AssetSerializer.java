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

            return new Asset(assetName, assetUrl);
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