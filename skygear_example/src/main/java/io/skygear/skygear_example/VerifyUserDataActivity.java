/*
 * Copyright 2018 Oursky Ltd.
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
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.LambdaResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.Record;

public class VerifyUserDataActivity extends AppCompatActivity {
    private static String LOG_TAG = VerifyUserDataActivity.class.getSimpleName();

    private Container skygear;
    private EditText verifyCodeInput;
    private Button verifyEmailButton;
    private Button verifyPhoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user_data);

        this.verifyCodeInput = (EditText) findViewById(R.id.verify_code_input);
        this.verifyEmailButton = (Button) findViewById(R.id.verify_email_button);
        this.verifyPhoneButton = (Button) findViewById(R.id.verify_phone_button);

        this.skygear = Container.defaultContainer(this);

        Boolean emailAvailable = false;
        Boolean phoneAvailable = false;
        Record currentUser = this.skygear.getAuth().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getData().containsKey("email") ? (String) currentUser.get("email") : null;
            String phone = currentUser.getData().containsKey("phone") ? (String) currentUser.get("phone") : null;
            emailAvailable = email != null && email.length() > 0;
            phoneAvailable = phone != null && phone.length() > 0;
        }

        this.verifyEmailButton.setEnabled(emailAvailable);
        this.verifyEmailButton.setText(emailAvailable ? R.string.verify_email : R.string.verify_email_missing);
        this.verifyPhoneButton.setEnabled(phoneAvailable);
        this.verifyPhoneButton.setText(phoneAvailable ? R.string.verify_phone : R.string.verify_phone_missing);
    }

    public void doVerifyUser(String recordKey) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Requesting Verification...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Verification Success")
                .setMessage("")
                .setPositiveButton("Back", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Verification Failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        Log.i(LOG_TAG, "doVerifyUser: Requested to verify: " + recordKey);

        this.skygear.getAuth().requestVerification(recordKey, new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                loading.dismiss();
                successDialog.setMessage("Succesfully requested verification");
                successDialog.show();
            }

            @Override
            public void onLambdaFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();
            }
        });
    }

    public void doVerifyEmail(View view) {
        this.doVerifyUser("email");
    }

    public void doVerifyPhone(View view) {
        this.doVerifyUser("phone");
    }

    public void doVerifyCode(View view) {
        String code = this.verifyCodeInput.getText().toString();

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Checking your code...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Verification Success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VerifyUserDataActivity.this.finish();
                    }
                })
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Verification Failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        Log.i(LOG_TAG, "doVerifyCode: Verifying code: " + code);

        this.skygear.getAuth().verifyUserWithCode(code, new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(Record user) {
                loading.dismiss();
                successDialog.setMessage("Success with user_id:\n" + user.getId());
                successDialog.show();
            }

            @Override
            public void onAuthFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();
            }
        });
    }
}
