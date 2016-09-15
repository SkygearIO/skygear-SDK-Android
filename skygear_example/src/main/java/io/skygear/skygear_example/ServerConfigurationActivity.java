package io.skygear.skygear_example;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;

public class ServerConfigurationActivity extends AppCompatActivity {

    private EditText endpointEditText;
    private EditText apiKeyEditText;
    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_configuration);

        this.skygear = Container.defaultContainer(this);

        this.endpointEditText = (EditText) findViewById(R.id.endpoint_edittext);
        this.apiKeyEditText = (EditText) findViewById(R.id.api_key_edittext);


        Configuration config = this.skygear.getConfig();
        this.endpointEditText.setText(config.getEndpoint());
        this.apiKeyEditText.setText(config.getApiKey());
    }

    public void doConfigure(View view) {
        String endpoint = this.endpointEditText.getText().toString();
        String apiKey = this.apiKeyEditText.getText().toString();

        if (endpoint.length() == 0 || apiKey.length() == 0) {
            return;
        }

        Configuration config = new Configuration.Builder()
                .endPoint(endpoint)
                .apiKey(apiKey)
                .build();

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
