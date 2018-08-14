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

import org.json.JSONObject;

/**
 * The Logout response handler wrapper.
 *
 * This wrapper wraps original logout response handler and auth resolver
 * so that the resolver will be called before original handler is called.
 */
class LogoutResponseHandlerWrapper extends ResponseHandler {
    private final AuthResolver resolver;
    private final LogoutResponseHandler originalHandler;

    /**
     * Instantiates a new Logout response handler wrapper.
     *
     * @param resolver        the auth resolver
     * @param originalHandler the original handler
     */
    public LogoutResponseHandlerWrapper(AuthResolver resolver, LogoutResponseHandler originalHandler) {
        super();
        this.resolver = resolver;
        this.originalHandler = originalHandler;
    }

    @Override
    public final void onSuccess(JSONObject result) {
        if (this.resolver != null) {
            this.resolver.resolveAuthUser(null, null);
        }
        if (this.originalHandler != null) {
            this.originalHandler.onSuccess(result);
        }
    }

    @Override
    public final void onFailure(Error error) {
        if (this.originalHandler != null) {
            this.originalHandler.onFailure(error);
        }
    }
}
