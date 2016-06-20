package io.skygear.skygear_example;

import io.skygear.skygear.SkygearApplication;

/**
 * Created by benlei on 3/6/2016.
 */
public class MyApplication extends SkygearApplication {
    @Override
    public String getSkygearEndpoint() {
        return "http://skygear.dev/";
    }

    @Override
    public String getApiKey() {
        return "changeme";
    }
}
