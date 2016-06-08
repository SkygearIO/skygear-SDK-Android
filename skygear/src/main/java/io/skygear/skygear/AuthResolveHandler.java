package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Auth resolve handler.
 *
 * An empty response handler for wrapping auth resolver.
 */
public class AuthResolveHandler implements Request.ResponseHandler {
    private AuthResolver authResolver;

    /**
     * Instantiates a new Auth resolve handler.
     *
     * @param authResolver the auth resolver
     */
    public AuthResolveHandler(AuthResolver authResolver) {
        super();
        this.authResolver = authResolver;
    }

    @Override
    public void onSuccess(JSONObject result) {
        try {
            String accessToken = result.getString("access_token");
            if (this.authResolver != null) {
                this.authResolver.resolveAuthToken(accessToken);
            }
        } catch (JSONException e) {
            this.onFail(new Request.Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Request.Error error) {
    }
}
