package io.skygear.skygear;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Auth response handler wrapper.
 *
 * This wrapper wraps original auth response handler and auth resolver
 * so that the resolver will be called before original handler is called.
 */
class AuthResponseHandlerWrapper implements Request.ResponseHandler {
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
            this.onFail(new Request.Error("Malformed server response"));
        }
    }

    @Override
    public void onFail(Request.Error error) {
        if (this.originalHandler != null) {
            this.originalHandler.onFail(error);
        }
    }
}
