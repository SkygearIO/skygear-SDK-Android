package io.skygear.skygear;

/**
 * Created by benlei on 1/6/2016.
 */
public class Container {
    private static final String DEFAULT_BASE_URL = "http://skygear.dev/";

    private String baseUrl;

    public Container() {
        this(DEFAULT_BASE_URL);
    }

    public Container(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
