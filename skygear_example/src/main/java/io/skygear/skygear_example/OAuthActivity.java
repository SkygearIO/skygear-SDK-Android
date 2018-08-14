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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.Record;
import io.skygear.skygear.sso.GetOAuthProviderProfilesResponseHandler;
import io.skygear.skygear.sso.LinkProviderResponseHandler;
import io.skygear.skygear.sso.OAuthOptionBuilder;
import io.skygear.skygear.sso.UnlinkProviderResponseHandler;

public class OAuthActivity extends AppCompatActivity {
    private static String LOG_TAG = OAuthActivity.class.getSimpleName();

    private Container skygear;
    private String selectedProvider = "facebook";
    private EditText accessTokenInput;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        this.accessTokenInput = (EditText) findViewById(R.id.access_token_input);

        this.skygear = Container.defaultContainer(this);
    }

    public void doLoginWithWebFlow(View view) {
        showLoading("Logging in...");

        this.skygear.getAuth().loginOAuthProvider(
                selectedProvider,
                new OAuthOptionBuilder().setScheme("skygearexample").getOption(),
                this, new AuthResponseHandler() {
                    @Override
                    public void onAuthSuccess(Record user) {
                        hideLoading();
                        showSuccessAlert("Login success with user_id:\n" + user.getId());
                    }

                    @Override
                    public void onAuthFail(Error error) {
                        hideLoading();
                        showSuccessAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    public void doLinkWithWebFlow(View view) {
        showLoading("Linking...");

        this.skygear.getAuth().linkOAuthProvider(
                selectedProvider,
                new OAuthOptionBuilder().setScheme("skygearexample").getOption(),
                this, new LinkProviderResponseHandler() {
                    @Override
                    public final void onSuccess() {
                        hideLoading();
                        showSuccessAlert("Link provider success");
                    }

                    @Override
                    public void onFail(Error error) {
                        hideLoading();
                        showSuccessAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    public void doLoginWithAccessToken(View view) {
        showLoading("Logging in...");

        this.skygear.getAuth().loginOAuthProviderWithAccessToken(
                selectedProvider,
                accessTokenInput.getText().toString(),
                new AuthResponseHandler() {
                    @Override
                    public void onAuthSuccess(Record user) {
                        hideLoading();
                        showSuccessAlert("Login success with user_id:\n" + user.getId());
                    }

                    @Override
                    public void onAuthFail(Error error) {
                        hideLoading();
                        showErrorAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    public void doLinkWithAccessToken(View view) {
        showLoading("Linking...");

        this.skygear.getAuth().linkOAuthProviderWithAccessToken(
                selectedProvider,
                accessTokenInput.getText().toString(),
                new LinkProviderResponseHandler() {
                    @Override
                    public final void onSuccess() {
                        hideLoading();
                        showSuccessAlert("Link provider successfully");

                    }

                    @Override
                    public void onFail(Error error) {
                        hideLoading();
                        showErrorAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    public void doUnlinkWithAccessToken(View view) {
        showLoading("Unlinking...");

        this.skygear.getAuth().unlinkOAuthProvider(
                selectedProvider,
                new UnlinkProviderResponseHandler() {
                    @Override
                    public final void onSuccess() {
                        hideLoading();
                        showSuccessAlert("Unlink provider successfully");

                    }

                    @Override
                    public void onFail(Error error) {
                        hideLoading();
                        showErrorAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    public void doGetProviderProfiles(View view) {
        showLoading("Getting provider profiles...");

        this.skygear.getAuth().getOAuthProviderProfiles(
                new GetOAuthProviderProfilesResponseHandler() {
                    @Override
                    public final void onSuccess(JSONObject result) {
                        hideLoading();
                        showSuccessAlert("Provider profiles data: \n" + result.toString());

                    }

                    @Override
                    public void onFail(Error error) {
                        hideLoading();
                        showErrorAlert("Fail with reason: \n" + error.getDetailMessage());
                    }
                });
    }

    private void showSuccessAlert(String message) {
        AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setNeutralButton("Dismiss", null)
                .create();

        successDialog.show();
    }

    private void showErrorAlert(String message) {
        AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setNeutralButton("Dismiss", null)
                .create();

        successDialog.show();
    }

    private void showLoading(String message) {
        hideLoading();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Loading");
        loadingDialog.setMessage(message);
        loadingDialog.show();
    }

    private void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
