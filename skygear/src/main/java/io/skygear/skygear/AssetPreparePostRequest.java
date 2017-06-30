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

import java.security.InvalidParameterException;
import java.util.HashMap;

/**
 * The Skygear Asset Prepare Post Request.
 *
 * <p>
 *     This request is for requesting a Asset Post Request to Skygear Server.
 * </p>
 */
public class AssetPreparePostRequest extends Request {
    private final Asset asset;

    /**
     * Instantiates a new Asset prepare post request.
     *
     * @param asset the asset
     */
    public AssetPreparePostRequest(Asset asset) {
        super("asset:put");

        this.data = new HashMap<>();
        this.asset = asset;

        this.updateData();
    }

    private void updateData() {
        this.data.put("filename", this.asset.getName());
        this.data.put("content-type", this.asset.getMimeType());
        this.data.put("content-size", this.asset.getSize());
    }

    @Override
    protected void validate() throws Exception {
        super.validate();

        String filename = (String) this.data.get("filename");
        if (filename == null || filename.length() == 0) {
            throw new InvalidParameterException("Missing filename");
        }

        String contentType = (String) this.data.get("content-type");
        if (contentType == null) {
            throw new InvalidParameterException("Missing MIME type of the asset");
        }

        long contentSize = (long) this.data.get("content-size");
        if (contentSize == 0) {
            throw new InvalidParameterException("Missing content size of the asset");
        }
    }
}
