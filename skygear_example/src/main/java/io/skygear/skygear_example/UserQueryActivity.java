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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.skygear.skygear.Container;
import io.skygear.skygear.Error;

public class UserQueryActivity extends AppCompatActivity {

    private EditText userEmailEditText;
    private TextView display;

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_query);

        this.userEmailEditText = (EditText) findViewById(R.id.user_email_edit_text);
        this.display = (TextView) findViewById(R.id.query_display);

        this.skygear = Container.defaultContainer(this);
    }

    private void display(String displayString) {
        this.display.setText(displayString);
    }

    @SuppressLint("DefaultLocale")
    public void doQuery(View view) {
        String email = this.userEmailEditText.getText().toString();
        if (email.length() == 0) {
            return;
        }

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Querying user...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Query success")
                .setMessage("")
                .setPositiveButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Query failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();
    }
}
