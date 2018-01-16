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

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import io.skygear.skygear.Error;

@RunWith(AndroidJUnit4.class)
public class WebOAuthUnitTest {

    @Test
    public void testResumeWebOAuthWithSuccessResponseURI() throws Exception {
        Uri resultURI = Uri.parse("skygearexample://skygeario.com/auth_handler?result=eyJyZXN1bHQiOiAiT0sifQ%3D%3D#_=_");

        WebOAuth.callback = new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    assertEquals("OK", result.getString("result"));
                } catch (JSONException e) {
                    fail("Should not get JSON error");
                }
            }

            @Override
            public void onFail(Error error) {
                fail("Should not get error callback");
            }
        };

        WebOAuth.resume(resultURI);
    }

    @Test
    public void testResumeWebOAuthWithErrorResponseURI() throws Exception {
        Uri resultURI = Uri.parse("skygear://skygeario.com/auth_handler?result=eyJlcnJvciI6IHsibmFtZSI6ICJJbnZhbGlkQXJndW1lbnQiLCAiY29kZSI6IDEwOCwgIm1lc3NhZ2UiOiAicHJvdmlkZXIgYWNjb3VudCBhbHJlYWR5IGxpbmtlZCB3aXRoIGV4aXN0aW5nIHVzZXIifX0%3D#_=_");

        WebOAuth.callback = new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFail(Error error) {
                assertEquals("provider account already linked with existing user", error.getDetailMessage());
            }
        };


        WebOAuth.resume(resultURI);
    }


    @Test
    public void testCancelWebOAuth() throws Exception {
        WebOAuth.callback = new WebOAuthHandler() {
            @Override
            public void onSuccess(JSONObject result) {
                fail("Should not get success callback");
            }

            @Override
            public void onFail(Error error) {
                assertEquals("User cancel the flow", error.getDetailMessage());
            }
        };


        WebOAuth.cancel();
    }

}
