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

package io.skygear.skygear.sso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OAuthOption {

    /**
     * The scheme of URL that use for return back to the app with oauth result
     */
    String scheme;

    /**
     * The domain of URL that use for return back to the app with oauth result
     */
    String domain;

    /**
     * OAuth scope
     */
    List<String> scope;

    /**
     * Extra options for genterating the oauth url
     */
    Map<String, Object> options;

    public OAuthOption(String scheme, String domain, List<String> scope, Map<String, Object> options) {
        this.scheme = scheme;
        this.domain = domain;
        this.scope = scope;
        this.options = options;
    }

    public void validate() throws Exception {
        if (this.scheme == null) {
            throw new InvalidParameterException("Scheme should not be null");
        }
    }

    public Map<String, Object> toLambdaArgs() {
        Map<String, Object> params = new HashMap<>();
        params.put("ux_mode", "android");
        params.put("callback_url", genCallbackURL());

        if (this.scope != null) {
            params.put("scope", new JSONArray(this.scope));
        }

        if (this.options != null) {
            params.put("options", new JSONObject(this.options));
        }
        return params;
    }

    String genCallbackURL() {
        return String.format("%s://%s/auth_handler", scheme, domain);
    }
}
