package io.skygear.skygear;

import org.json.JSONObject;

/**
 * The Logout response handler wrapper.
 *
 * This wrapper wraps original logout response handler and auth resolver
 * so that the resolver will be called before original handler is called.
 */
class LogoutResponseHandlerWrapper implements Request.ResponseHandler {
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
    public void onSuccess(JSONObject result) {
        if (this.resolver != null) {
            this.resolver.resolveAuthUser(null);
        }
        if (this.originalHandler != null) {
            this.originalHandler.onSuccess(result);
        }
    }

    @Override
    public void onFail(Request.Error error) {
        if (this.originalHandler != null) {
            this.originalHandler.onFail(error);
        }
    }
}
