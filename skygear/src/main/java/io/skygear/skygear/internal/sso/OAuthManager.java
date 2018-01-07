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

package io.skygear.skygear.internal.sso;

import android.app.Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import io.skygear.skygear.AuthContainer;
import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Error;
import io.skygear.skygear.LambdaResponseHandler;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordSerializer;

public class OAuthManager {
    private static String LOG_TAG = OAuthManager.class.getSimpleName();

    private static final String DEFAULT_CALLBACK_SCHEME = "skygear";
    private static final String DEFAULT_CALLBACK_DOMAIN = "skygeario.com";

    private enum OAuthActionType {
        LOGIN,
        LINK
    }

    /**
     * Login oauth provider by web oauth flow.
     *
     * @param authContainer the auth container
     * @param providerID    the provider id, e.g. google, facebook
     * @param options       the options
     * @param activity      a valid activity context
     * @param handler       the response handler
     */
    public void loginProvider(AuthContainer authContainer, String providerID, Map<String, Object> options, final Activity activity, final AuthResponseHandler handler) {
        this.oauthFlowWithProvider(OAuthActionType.LOGIN, authContainer, providerID, options, activity, loginWebOAuthHandler(authContainer, handler));
    }

    private void oauthFlowWithProvider(OAuthActionType actionType, AuthContainer authContainer, String providerID, Map<String, Object> options, final Activity activity, final WebOAuthHandler handler) {
        authContainer.getContainer().callLambdaFunction(authURLWithAction(actionType, providerID),
                genAuthURLParams(options), new LambdaResponseHandler() {
                    @Override
                    public void onLambdaSuccess(JSONObject result) {
                        try {
                            String authURL = result.getString("auth_url");
                            // start web oauth flow after getting the auth url
                            if (handler != null) {
                                WebOAuth.start(activity, authURL, handler);
                            }
                        } catch (JSONException e) {
                            if (handler != null) {
                                handler.onFail(new Error("Malformed server response"));
                            }
                        }
                    }

                    @Override
                    public void onLambdaFail(Error error) {
                        if (handler != null) {
                            handler.onFail(error);
                        }
                    }
                });
    }

    private WebOAuthHandler loginWebOAuthHandler(final AuthContainer authContainer, final AuthResponseHandler handler) {
        return new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONObject profile = result.optJSONObject("profile");
                    String accessToken = result.getString("access_token");
                    Record authUser = RecordSerializer.deserialize(profile);
                    authContainer.resolveAuthUser(authUser, accessToken);
                    if (handler != null) {
                        handler.onAuthSuccess(authUser);
                    }
                } catch (JSONException e) {
                    if (handler != null) {
                        handler.onFail(new Error("Malformed server response"));
                    }
                }
            }

            @Override
            public void onFail(Error error) {
                if (handler != null) {
                    handler.onAuthFail(error);
                }
            }
        };
    }

    private String authURLWithAction(OAuthActionType action, String provider) {
        switch (action) {
            case LOGIN:
                return String.format("sso/%s/login_auth_url", provider);
            case LINK:
                return String.format("sso/%s/link_auth_url", provider);
            default:
                throw new InvalidParameterException("Invalid oauth flow action");
        }
    }

    private Map<String, Object> genAuthURLParams(Map<String, Object> options) {
        Map<String, Object> params = new HashMap<>();
        params.put("ux_mode", "android");
        params.put("callback_url", genCallbackURL(options));

        if (options.containsKey("scope")) {
            params.put("scope", options.get("scope"));
        }

        if (options.containsKey("options")) {
            params.put("options", options.get("options"));
        }

        return params;
    }

    private String genCallbackURL(Map<String, Object> options) {
        String scheme = DEFAULT_CALLBACK_SCHEME;
        if (options.get("scheme") instanceof String) {
            scheme = (String)options.get("scheme");
        }

        String domain = DEFAULT_CALLBACK_DOMAIN;
        if (options.get("domain") instanceof String) {
            scheme = (String)options.get("scheme");
        }

        return String.format("%s://%s/auth_handler", scheme, domain);
    }
}
