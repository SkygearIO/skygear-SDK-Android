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

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;

public class ServerConfigurationActivity extends AppCompatActivity {

    private EditText endpointEditText;
    private EditText apiKeyEditText;
    private CheckBox encryptUserDataCheckbox;
    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_configuration);

        this.skygear = Container.defaultContainer(this);

        this.endpointEditText = (EditText) findViewById(R.id.endpoint_edittext);
        this.apiKeyEditText = (EditText) findViewById(R.id.api_key_edittext);
        this.encryptUserDataCheckbox = findViewById(R.id.encrypt_user_data_checkbox);

        Configuration config = this.skygear.getConfig();
        if (config != null) {
            this.endpointEditText.setText(config.getEndpoint());
            this.apiKeyEditText.setText(config.getApiKey());
            this.encryptUserDataCheckbox.setChecked(config.encryptCurrentUserData());
        } else {
            this.endpointEditText.setText("");
            this.apiKeyEditText.setText("");
            this.encryptUserDataCheckbox.setChecked(false);
        }

    }

    public void doConfigure(View view) {
        String endpoint = this.endpointEditText.getText().toString().trim();
        String apiKey = this.apiKeyEditText.getText().toString().trim();
        boolean encryptUserData = this.encryptUserDataCheckbox.isChecked();

        if (endpoint.length() == 0 || apiKey.length() == 0) {
            return;
        }

        Configuration.Builder configBuilder = new Configuration.Builder()
                .endPoint(endpoint)
                .apiKey(apiKey)
                .encryptCurrentUserData(encryptUserData);

        Configuration config = configBuilder.build();

        this.skygear.configure(config);
        this.saveConfiguration(config);

        new AlertDialog.Builder(this)
                .setTitle("Server Configuration")
                .setMessage(String.format("Success!\n\nEndpoint:\n%s\n\nAPI Key:\n%s", endpoint, apiKey))
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ServerConfigurationActivity.this.finish();
                    }
                })
                .show();
    }

    private void saveConfiguration(Configuration config) {
        ServerConfigurationPreference pref = new ServerConfigurationPreference(this);
        pref.update(config);
    }
}
