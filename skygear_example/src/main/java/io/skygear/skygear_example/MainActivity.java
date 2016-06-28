package io.skygear.skygear_example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.User;

public class MainActivity extends AppCompatActivity {
    private Container skygear;
    private TextView endpointDisplay;
    private TextView apiKeyDisplay;
    private TextView accessTokenDisplay;
    private TextView userIdDisplay;
    private TextView usernameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.endpointDisplay = (TextView) findViewById(R.id.endpoint_display);
        this.apiKeyDisplay = (TextView) findViewById(R.id.api_key_display);
        this.accessTokenDisplay = (TextView) findViewById(R.id.access_token_display);
        this.userIdDisplay = (TextView) findViewById(R.id.user_id_display);
        this.usernameDisplay = (TextView) findViewById(R.id.username_display);

        this.skygear = Container.defaultContainer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration skygearConfig = this.skygear.getConfig();

        this.endpointDisplay.setText(skygearConfig.endpoint);
        this.apiKeyDisplay.setText(skygearConfig.apiKey);

        User currentUser = this.skygear.getCurrentUser();
        if (currentUser != null) {
            this.accessTokenDisplay.setText(currentUser.accessToken);
            this.userIdDisplay.setText(currentUser.userId);
            this.usernameDisplay.setText(currentUser.username);
        } else {
            this.accessTokenDisplay.setText(R.string.undefined);
            this.userIdDisplay.setText(R.string.undefined);
            this.usernameDisplay.setText(R.string.undefined);
        }

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

    public void goRecordCURD(View view) {
        startActivity(new Intent(this, RecordActivity.class));
    }
}
