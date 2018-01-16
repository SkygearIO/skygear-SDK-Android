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
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.skygear.skygear.Error;

public class WebOAuth {
    private static String LOG_TAG = WebOAuth.class.getSimpleName();
    static WebOAuthHandler callback;

    public static void start(Activity activity, String authURL, WebOAuthHandler completionHandler) {
        callback = completionHandler;
        Intent intent = AuthenticationActivity.createStartIntent(activity, authURL);
        activity.startActivity(intent);
    }

    public static void resume(Uri resultURL) {
        Log.d(LOG_TAG, resultURL.toString());

        String encodedStr = resultURL.getQueryParameter("result");
        byte[] bytes = Base64.decode(encodedStr, Base64.DEFAULT);
        String resultJSON = new String(bytes);

        Log.d(LOG_TAG, resultJSON);
        try {
            JSONObject jsonObject = new JSONObject(resultJSON);
            if (jsonObject.has("error")) {
                callback.onFail(new Error(jsonObject.getJSONObject("error")));
            } else {
                callback.onSuccess(jsonObject);
            }
        } catch (JSONException e) {
            callback.onFail(new Error("Malformed server response"));
        }
    }

    public static void cancel() {
        callback.onFail(new Error("User cancel the flow"));
    }
}