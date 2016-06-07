package io.skygear.skygear;

import org.json.JSONObject;

import java.util.Map;

public class Request {
    public final String action;
    public final Map<String, Object> data;
    public ResponseHandler responseHandler;

    public Request(String action, Map<String, Object>data) {
        this(action, data, null);
    }

    public Request(String action, Map<String, Object>data, ResponseHandler responseHandler) {
        this.action = action;
        this.data = data;
        this.responseHandler = responseHandler;
    }

    public interface ResponseHandler {
        void onSuccess(JSONObject result);
        void onFail(Error error);
    }

    public static class Error extends Exception {
        public Error(String detailMessage) {
            super(detailMessage);
        }
    }
}
