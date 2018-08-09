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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
     * The Asset MIME Type.
     */
    final String mimeType;

    /**
     * The Asset data size
     */
    final long size;

    /**
     * The Asset data input stream
     */
    final InputStream inputStream;

    /**
     * Instantiates a new Asset with name, MIME type and file data.
     *
     * @deprecated use {@link Asset.Builder} instead.
     *
     * @param name     the name
     * @param mimeType the MIME type
     * @param data     the data
     */
    @Deprecated
    public Asset(String name, String mimeType, byte[] data) {
        super();
        this.name = name;
        this.mimeType = mimeType;
        this.size = data.length;
        this.inputStream = new ByteArrayInputStream(data);
    }

    Asset(String name, String mimeType, long size, InputStream inputStream) {
        this.name = name;
        this.mimeType = mimeType;
        this.size = size;
        this.inputStream = inputStream;
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
        this.size = 0;
        this.inputStream = null;
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
        return this.url == null;
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
        return this.size;
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

    /**
     * Asset builder
     */
    public static class Builder {
        private Context context;
        private String name;
        private String mimeType;
        private Long size;
        private Uri uri;
        private byte[] data;
        private InputStream inputStream;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder setSize(Long size) {
            this.size = size;
            return this;
        }

        public Builder setUri(Uri uri) {
            this.uri = uri;
            return this;
        }

        public Builder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public Builder setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Asset build() {
            ensureStateIsNonNull(this.name, "Name");

            if (this.inputStream != null) {
                return this.buildFromInputStream();
            }

            if (this.data != null) {
                return this.buildFromByteArray();
            }

            if (this.uri != null) {
                return this.buildFromUri();
            }

            throw new IllegalStateException("Either inputStream, data or uri must be set first");
        }

        private Asset buildFromInputStream() {
            ensureStateIsNonNull(this.mimeType, "MimeType");
            ensureStateIsNonNull(this.size, "Size");

            return new Asset(this.name, this.mimeType, this.size, this.inputStream);
        }

        private Asset buildFromByteArray() {
            ensureStateIsNonNull(this.mimeType, "MimeType");

            long size = this.data.length;
            InputStream inputStream = new ByteArrayInputStream(this.data);

            return new Asset(this.name, this.mimeType, size, inputStream);
        }

        private Asset buildFromUri() {
            ensureStateIsNonNull(this.context, "Context");

            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream;
            long size;
            try {
                inputStream = contentResolver.openInputStream(uri);
                ParcelFileDescriptor fd = contentResolver.openFileDescriptor(this.uri, "r");
                size = fd.getStatSize();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            String mimeType = contentResolver.getType(uri);

            return new Asset(this.name, mimeType, size, inputStream);
        }

        private static void ensureStateIsNonNull(Object state, String name) {
            if (state == null) {
                throw new IllegalStateException(name + " must be set first");
            }
        }
    }
}
