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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AuthenticationActivity extends AppCompatActivity {

    static final String EXTRA_AUTH_URL = "io.skygear.skygear.sso.EXTRA_AUTH_URL";
    static final String EXTRA_AUTH_STARTED = "io.skygear.skygear.sso.EXTRA_AUTH_STARTED";

    private Boolean mAuthStarted = false;

    public static Intent createStartIntent(Context context, String authURL) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(EXTRA_AUTH_URL, authURL);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public static Intent createResponseHandlingIntent(Context context, Uri responseUri) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setData(responseUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mAuthStarted = savedInstanceState.getBoolean(EXTRA_AUTH_STARTED, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mAuthStarted) {
            if (getIntent().getExtras() == null) {
                // Started the activity unexpectedly
                // This will happen if browser redirect back to application twice in one login flow
                finish();
                return;
            }
            mAuthStarted = true;
            launchAuthIntent();
            return;
        }

        if (getIntent().getData() != null) {
            WebOAuth.resume(getIntent().getData());
        } else {
            WebOAuth.cancel();
        }

        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_AUTH_STARTED, mAuthStarted);
    }

    private void launchAuthIntent() {
        Bundle extras = getIntent().getExtras();
        String authURL = extras.getString(EXTRA_AUTH_URL);
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(authURL)
        );
        startActivity(intent);
    }
}
