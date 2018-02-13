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

import io.skygear.skygear.AuthContainer;
import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Error;
import io.skygear.skygear.LambdaResponseHandler;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordSerializer;
import io.skygear.skygear.sso.GetOAuthProviderProfilesResponseHandler;
import io.skygear.skygear.sso.LinkProviderResponseHandler;
import io.skygear.skygear.sso.OAuthOption;
import io.skygear.skygear.sso.UnlinkProviderResponseHandler;

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
     * @param handler       the auth response handler
     */
    public void loginProvider(final AuthContainer authContainer, String providerID, OAuthOption options, final Activity activity, final AuthResponseHandler handler) {
        this.oauthFlowWithProvider(OAuthActionType.LOGIN, authContainer, providerID, options, activity, new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                handleLoginResponse(authContainer, result, handler);
            }

            @Override
            public void onFail(Error error) {
                if (handler != null) {
                    handler.onAuthFail(error);
                }
            }
        });
    }

    /**
     * Link oauth provider by web oauth flow.
     *
     * @param authContainer the auth container
     * @param providerID    the provider id, e.g. google, facebook
     * @param options       the options
     * @param activity      a valid activity context
     * @param handler       the link provider response handler
     */
    public void linkProvider(AuthContainer authContainer, String providerID, OAuthOption options, final Activity activity, final LinkProviderResponseHandler handler) {
        this.oauthFlowWithProvider(OAuthActionType.LINK, authContainer, providerID, options, activity, new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                if (handler != null) {
                    handler.onSuccess();
                }
            }

            @Override
            public void onFail(Error error) {
                if (handler != null) {
                    handler.onFail(error);
                }
            }
        });
    }

    /**
     * Login oauth provider with provider access token.
     *
     * @param authContainer the auth container
     * @param providerID    the provider id, e.g. google, facebook
     * @param accessToken   access token from provider
     * @param handler       the auth response handler
     */
    public void loginProviderWithAccessToken(final AuthContainer authContainer, String providerID, final String accessToken, final AuthResponseHandler handler) {
        authContainer.getContainer().callLambdaFunction(
                authURLWithAccessToken(OAuthActionType.LOGIN, providerID),
                new HashMap<String, Object>() {{
                    put("access_token", accessToken);
                }}, new LambdaResponseHandler() {
                    @Override
                    public void onLambdaSuccess(JSONObject result) {
                        handleLoginResponse(authContainer, result, handler);
                    }

                    @Override
                    public void onLambdaFail(Error error) {
                        if (handler != null) {
                            handler.onFail(error);
                        }
                    }
                });

    }

    /**
     * Link oauth provider with provider access token.
     *
     * @param authContainer the auth container
     * @param providerID    the provider id, e.g. google, facebook
     * @param accessToken   access token from provider
     * @param handler       the link provider response handler
     */
    public void linkProviderWithAccessToken(AuthContainer authContainer, String providerID, final String accessToken, final LinkProviderResponseHandler handler) {
        authContainer.getContainer().callLambdaFunction(
                authURLWithAccessToken(OAuthActionType.LINK, providerID),
                new HashMap<String, Object>() {{
                    put("access_token", accessToken);
                }}, new LambdaResponseHandler() {
                    @Override
                    public void onLambdaSuccess(JSONObject result) {
                        handleLinkReponse(result, handler);
                        if (handler != null) {
                            handler.onSuccess();
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

    /**
     * Unlink oauth provider.
     *
     * @param authContainer the auth container
     * @param providerID    the provider id, e.g. google, facebook
     * @param handler       the link provider response handler
     */
    public void unlinkProvider(AuthContainer authContainer, String providerID, final UnlinkProviderResponseHandler handler) {
        authContainer.getContainer().callLambdaFunction(
                String.format("sso/%s/unlink", providerID),
                new LambdaResponseHandler() {
                    @Override
                    public void onLambdaSuccess(JSONObject result) {
                        if (handler != null) {
                            handler.onSuccess();
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

    /**
     * Get oauth provider profiles.
     *
     * @param authContainer the auth container
     * @param handler       return JSONObject that contains provider's user profiles
     *                      key is the provider id, value is the JSONObject of provider's profile response
     */
    public void getProviderProfiles(AuthContainer authContainer, final GetOAuthProviderProfilesResponseHandler handler) {
        authContainer.getContainer().callLambdaFunction(
                "sso/provider_profiles",
                new LambdaResponseHandler() {
                    @Override
                    public void onLambdaSuccess(JSONObject result) {
                        if (handler != null) {
                            handler.onSuccess(result);
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

    private void oauthFlowWithProvider(OAuthActionType actionType, AuthContainer authContainer, String providerID, OAuthOption options, final Activity activity, final WebOAuthHandler handler) {
        try {
            if (options == null) {
                throw new InvalidParameterException("Options should not be null");
            }
            options.validate();
        } catch (Exception e) {
            if (handler != null) {
                handler.onFail(new Error(e.getMessage()));
            }
            return;
        }

        authContainer.getContainer().callLambdaFunction(authURL(actionType, providerID),
                options.toLambdaArgs(), new LambdaResponseHandler() {
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

    private void handleLoginResponse(AuthContainer authContainer, JSONObject response, AuthResponseHandler handler) {
        try {
            if (response.has("error")) {
                if (handler != null) {
                    handler.onFail(new Error(response.getJSONObject("error")));
                }
                return;
            }
            JSONObject result = response.getJSONObject("result");
            JSONObject profile = result.getJSONObject("profile");
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

    private void handleLinkReponse(JSONObject response, LinkProviderResponseHandler handler) {
        try {
            if (response.has("error")) {
                handler.onFail(new Error(response.getJSONObject("error")));
            } else {
                handler.onSuccess();
            }
        } catch (JSONException e) {
            handler.onFail(new Error("Malformed server response"));
        }
    }

    private String authURL(OAuthActionType action, String provider) {
        switch (action) {
            case LOGIN:
                return String.format("sso/%s/login_auth_url", provider);
            case LINK:
                return String.format("sso/%s/link_auth_url", provider);
            default:
                throw new InvalidParameterException("Invalid oauth flow action");
        }
    }

    private String authURLWithAccessToken(OAuthActionType action, String provider) {
        switch (action) {
            case LOGIN:
                return String.format("sso/%s/login", provider);
            case LINK:
                return String.format("sso/%s/link", provider);
            default:
                throw new InvalidParameterException("Invalid oauth flow action");
        }
    }
}
