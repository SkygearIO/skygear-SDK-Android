package io.skygear.skygear_example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

        this.skygear = Container.defaultContainer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration skygearConfig = this.skygear.getConfig();

        this.endpointDisplay.setText(skygearConfig.endpoint);
        this.apiKeyDisplay.setText(skygearConfig.apiKey);
    }

    public void goSignup(View v) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void goLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void goLogout(View view) {
        startActivity(new Intent(this, LogoutActivity.class));
    }
}
