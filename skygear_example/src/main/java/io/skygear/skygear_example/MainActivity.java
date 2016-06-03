package io.skygear.skygear_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;

public class MainActivity extends AppCompatActivity {
    private Container skygear;
    private TextView endpointDisplay;
    private TextView apiKeyDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.endpointDisplay = (TextView) findViewById(R.id.endpoint_display);
        this.apiKeyDisplay = (TextView) findViewById(R.id.api_key_display);

        this.skygear = Container.defaultContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration skygearConfig = this.skygear.getConfig();

        this.endpointDisplay.setText(String.format("Endpoint: %s", skygearConfig.endpoint));
        this.apiKeyDisplay.setText(String.format("API Key: %s", skygearConfig.apiKey));
    }
}
