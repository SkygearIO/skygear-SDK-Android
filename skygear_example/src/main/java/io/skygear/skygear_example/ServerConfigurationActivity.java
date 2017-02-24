package io.skygear.skygear_example;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.RegisterDeviceRequest;
import io.skygear.skygear.gcm.RegistrationIntentService;

public class ServerConfigurationActivity extends AppCompatActivity {

    private EditText endpointEditText;
    private EditText apiKeyEditText;
    private EditText gcmSenderIdEditText;
    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_configuration);

        this.skygear = Container.defaultContainer(this);

        this.endpointEditText = (EditText) findViewById(R.id.endpoint_edittext);
        this.apiKeyEditText = (EditText) findViewById(R.id.api_key_edittext);
        this.gcmSenderIdEditText = (EditText) findViewById(R.id.gcm_sender_id_edittext);

        Configuration config = this.skygear.getConfig();
        this.endpointEditText.setText(config.getEndpoint());
        this.apiKeyEditText.setText(config.getApiKey());

        if (config.getGcmSenderId() != null) {
            this.gcmSenderIdEditText.setText(config.getGcmSenderId());
        }
    }

    public void doConfigure(View view) {
        String endpoint = this.endpointEditText.getText().toString().trim();
        String apiKey = this.apiKeyEditText.getText().toString().trim();
        String gcmSenderId = this.gcmSenderIdEditText.getText().toString().trim();

        if (endpoint.length() == 0 || apiKey.length() == 0) {
            return;
        }

        Configuration.Builder configBuilder = new Configuration.Builder()
                .endPoint(endpoint)
                .apiKey(apiKey);

        if (gcmSenderId.length() > 0) {
            configBuilder.gcmSenderId(gcmSenderId);
        }

        Configuration config = configBuilder.build();

        this.skygear.configure(config);
        this.saveConfiguration(config);

        if (this.skygear.getGcmSenderId() != null) {
            Intent gcmTokenRegisterIntent = new Intent(this, RegistrationIntentService.class);
            this.startService(gcmTokenRegisterIntent);
        }

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
