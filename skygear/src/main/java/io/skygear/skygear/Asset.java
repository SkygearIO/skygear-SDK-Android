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
 * The Skygear Asset Model.
 */
public class Asset {
    /**
     * The Name.
     */
    String name;

    /**
     * The Asset Url.
     */
    String url;

    /**
     * The Asset Data.
     */
    byte[] data;

    /**
     * The indicator whether it is pending upload.
     */
    boolean pendingUpload;

    /**
     * The Asset MIME Type.
     */
    String mimeType;

    /**
     * Instantiates a new Asset with name, MIME type and file data.
     *
     * @param name     the name
     * @param mimeType the MIME type
     * @param data     the data
     */
    public Asset(String name, String mimeType, byte[] data) {
        super();

        this.name = name;
        this.mimeType = mimeType;
        this.data = data;
        this.pendingUpload = true;
    }

    /**
     * Instantiates a new Asset with name and url.
     *
     * @param name     the name
     * @param url      the url
     * @param mimeType the MIME type
     */
    public Asset(String name, String url, String mimeType) {
        super();

        this.name = name;
        this.url = url;
        this.mimeType = mimeType;
        this.pendingUpload = false;
    }

    /**
     * Gets asset name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets asset url.
     *
     * <p>
     *     This will set to null if the asset is not yet uploaded.
     * </p>
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets a boolean whether it is pending to upload.
     *
     * @return the boolean
     */
    public boolean isPendingUpload() {
        return pendingUpload;
    }

    /**
     * Gets MIME type.
     *
     * @return the mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the size of data.
     *
     * @return the size
     */
    public long getSize() {
        if (this.data == null) {
            return 0;
        }

        return this.data.length;
    }

    /**
     * Get the asset data
     *
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Serializes the asset.
     *
     * @return the JSON object
     */
    public JSONObject toJson() {
        return AssetSerializer.serialize(this);
    }

    /**
     * Deserializes the asset.
     *
     * @param jsonObject the JSON object
     * @return the asset
     * @throws JSONException the json exception
     */
    public static Asset fromJson(JSONObject jsonObject) throws JSONException {
        return AssetSerializer.deserialize(jsonObject);
    }
}
