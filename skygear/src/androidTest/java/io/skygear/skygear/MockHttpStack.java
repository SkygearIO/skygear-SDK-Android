package io.skygear.skygear;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.toolbox.HttpStack;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

class MockHttpStack implements HttpStack {
    static HttpResponse getDummyHttpResponse () {
        BasicHttpResponse response = new BasicHttpResponse(
                new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), 200, "OK")
        );

        try {
            response.setEntity(new StringEntity("{}"));
        } catch (UnsupportedEncodingException e) {
        }

        return response;
    }

    private final RequestValidator validator;
    private final MockResponder responder;

    public MockHttpStack() {
        this(null, null);
    }

    public MockHttpStack(RequestValidator validator) {
        this(validator, null);
    }

    public MockHttpStack(MockResponder responder) {
        this(null, responder);
    }

    public MockHttpStack(RequestValidator validator, MockResponder responder) {
        this.validator = validator;
        this.responder = responder;
    }

    @Override
    public HttpResponse performRequest(com.android.volley.Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        if (this.validator != null) {
            this.validator.validate(request, additionalHeaders);
        }

        HttpResponse response = null;
        if (this.responder != null) {
            response = this.responder.getResponse(request, additionalHeaders);
        }

        return response != null ? response : MockHttpStack.getDummyHttpResponse();
    }

    interface RequestValidator {
        void validate(Request request, Map<String, String> additionalHeaders) throws AuthFailureError;
    }

    interface MockResponder {
        HttpResponse getResponse(Request request,  Map<String, String> header)  throws AuthFailureError;
    }
}
