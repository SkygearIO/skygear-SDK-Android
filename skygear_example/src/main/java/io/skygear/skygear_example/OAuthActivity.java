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

package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.Record;

public class OAuthActivity extends AppCompatActivity {
    private static String LOG_TAG = OAuthActivity.class.getSimpleName();

    private Container skygear;
    private String selectedProvider = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);
        this.skygear = Container.defaultContainer(this);
        this.restoreServerConfiguration();
    }

    private void restoreServerConfiguration() {
        ServerConfigurationPreference pref = new ServerConfigurationPreference(this);
        Configuration config = pref.get();

        if (config != null) {
            this.skygear.configure(config);
        }
    }

    public void doLoginWithWebFlow(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging in...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Login success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Login failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        this.skygear.getAuth().loginOAuthProvider(
                selectedProvider,
                new HashMap<String, Object>() {{
                    put("scheme", "skygearexample");
                }}, this, new AuthResponseHandler() {
                    @Override
                    public void onAuthSuccess(Record user) {
                        Log.d(LOG_TAG, "onAuthSuccess");
                        loading.dismiss();

                        successDialog.setMessage("Success with user_id:\n" + user.getId());
                        successDialog.show();
                    }

                    @Override
                    public void onAuthFail(Error error) {
                        loading.dismiss();

                        failDialog.setMessage("Fail with reason: \n" + error.getDetailMessage());
                        failDialog.show();
                    }
                });

    }
}
