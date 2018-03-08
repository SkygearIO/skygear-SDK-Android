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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.json.JSONObject;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.LambdaResponseHandler;

public class ResetPasswordActivity extends AppCompatActivity {
    private static String LOG_TAG = ResetPasswordActivity.class.getSimpleName();

    private Container skygear;
    private EditText confirmPasswordInput;
    private EditText newPasswordInput;
    private EditText codeInput;
    private EditText emailInput;
    private EditText userIdInput;
    private EditText expireAtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        this.confirmPasswordInput = findViewById(R.id.confirm_password_input);
        this.newPasswordInput = findViewById(R.id.new_password_input);
        this.codeInput = findViewById(R.id.code);
        this.emailInput = findViewById(R.id.email);
        this.userIdInput = findViewById(R.id.user_id);
        this.expireAtInput = findViewById(R.id.expire_at);

        this.skygear = Container.defaultContainer(this);
    }


    public void doForgotPassword(View v) {
        final String email = this.emailInput.getText().toString();
        final ProgressDialog loading = createLoading();
        this.skygear.getAuth().forgotPassword(email, new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                loading.dismiss();
                new AlertDialog.Builder(ResetPasswordActivity.this)
                    .setTitle("Forgot Password success")
                    .setMessage(email)
                    .setPositiveButton("Dismiss", null)
                    .create().show();

                Log.i(LOG_TAG, "onAuthSuccess");
            }

            @Override
            public void onLambdaFail(Error error) {
                loading.dismiss();
                new AlertDialog.Builder(ResetPasswordActivity.this)
                        .setTitle("Forgot Password failed.")
                        .setMessage(email)
                        .setNeutralButton("Dismiss", null)
                        .create().show();
            }
        });
    }

    public void doResetPassword(View v) {
        String confirmPassword = this.confirmPasswordInput.getText().toString();
        String newPassword = this.newPasswordInput.getText().toString();
        String code = this.codeInput.getText().toString();
        String userId = this.userIdInput.getText().toString();
        long expireAt = Long.parseLong(this.expireAtInput.getText().toString());

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Change Password failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        if (!newPassword.equals(confirmPassword)) {
            failDialog.setMessage("New password does not match confirm password");
            failDialog.show();
            return;
        }

        final ProgressDialog loading = createLoading();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Change Password success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResetPasswordActivity.this.finish();
                    }
                })
                .create();

        Log.i(LOG_TAG, "doResetPassword: Change with new password: " + newPassword);
        Log.i(LOG_TAG, "doResetPassword: Change with code: " + code);

        this.skygear.getAuth().resetPassword(userId, code, expireAt, newPassword, new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                loading.dismiss();
                successDialog.setMessage("Successfully reset password");
                successDialog.show();

                Log.i(LOG_TAG, "onAuthSuccess");
            }

            @Override
            public void onLambdaFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();

                Log.i(LOG_TAG, "onAuthFail: Fail with reason: " + error.getMessage());
            }
        });
    }

    private ProgressDialog createLoading() {
        ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Changing Password up...");
        loading.show();
        return loading;
    }
}
