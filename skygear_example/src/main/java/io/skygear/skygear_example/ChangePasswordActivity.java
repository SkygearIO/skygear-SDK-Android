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

import java.util.HashMap;
import java.util.Map;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.Record;

public class ChangePasswordActivity extends AppCompatActivity {
    private static String LOG_TAG = ChangePasswordActivity.class.getSimpleName();

    private Container skygear;
    private EditText oldPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.oldPasswordInput = (EditText) findViewById(R.id.old_password_input);
        this.newPasswordInput = (EditText) findViewById(R.id.new_password_input);
        this.confirmPasswordInput = (EditText) findViewById(R.id.confirm_password_input);

        this.skygear = Container.defaultContainer(this);
    }

    public void doChangePassword(View v) {
        String oldPassword = this.oldPasswordInput.getText().toString();
        String newPassword = this.newPasswordInput.getText().toString();
        String confirmPassword = this.confirmPasswordInput.getText().toString();

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

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Changing Password up...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Change Password success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ChangePasswordActivity.this.finish();
                    }
                })
                .create();

        Log.i(LOG_TAG, "doChangePassword: Change with old password: " + oldPassword);
        Log.i(LOG_TAG, "doChangePassword: Change with new password: " + newPassword);

        this.skygear.getAuth().changePassword(newPassword, oldPassword, new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(Record user) {
                loading.dismiss();
                successDialog.setMessage("Successfully changed password");
                successDialog.show();

                Log.i(LOG_TAG, "onAuthSuccess");
            }

            @Override
            public void onAuthFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();

                Log.i(LOG_TAG, "onAuthFail: Fail with reason: " + error.getMessage());
            }
        });
    }
}
