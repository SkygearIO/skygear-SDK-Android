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
 * The Auth response handler wrapper.
 *
 * This wrapper wraps original auth response handler and auth resolver
 * so that the resolver will be called before original handler is called.
 */
class AuthResponseHandlerWrapper implements ResponseHandler {
    private final AuthResolver authResolver;
    private final AuthResponseHandler originalHandler;

    /**
     * Instantiates a new Auth response handler wrapper.
     *
     * @param resolver         the auth resolver
     * @param originalHandler  the original handler
     */
    public AuthResponseHandlerWrapper(AuthResolver resolver, AuthResponseHandler originalHandler) {
        super();
        this.authResolver = resolver;
        this.originalHandler = originalHandler;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            User authUser = UserSerializer.deserialize(result);
            if (this.authResolver != null) {
                this.authResolver.resolveAuthUser(authUser);
            }
            if (this.originalHandler != null) {
                this.originalHandler.onSuccess(result);
            }
        } catch (JSONException e) {
            this.onFail(new Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Error error) {
        if (this.originalHandler != null) {
            this.originalHandler.onFail(error);
        }
    }
}
