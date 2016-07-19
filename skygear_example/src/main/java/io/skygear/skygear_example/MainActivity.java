package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.LogoutResponseHandler;
import io.skygear.skygear.User;

public class MainActivity extends AppCompatActivity {
    private Container skygear;
    private TextView endpointDisplay;
    private TextView apiKeyDisplay;
    private TextView accessTokenDisplay;
    private TextView userIdDisplay;
    private TextView emailDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.endpointDisplay = (TextView) findViewById(R.id.endpoint_display);
        this.apiKeyDisplay = (TextView) findViewById(R.id.api_key_display);
        this.accessTokenDisplay = (TextView) findViewById(R.id.access_token_display);
        this.userIdDisplay = (TextView) findViewById(R.id.user_id_display);
        this.emailDisplay = (TextView) findViewById(R.id.email_display);

        this.skygear = Container.defaultContainer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration skygearConfig = this.skygear.getConfig();

        this.endpointDisplay.setText(skygearConfig.endpoint);
        this.apiKeyDisplay.setText(skygearConfig.apiKey);

        this.updateUserInfoDisplay();
    }

    private void updateUserInfoDisplay() {
        User currentUser = this.skygear.getCurrentUser();
        if (currentUser != null) {
            this.accessTokenDisplay.setText(currentUser.getAccessToken());
            this.userIdDisplay.setText(currentUser.getId());
            this.emailDisplay.setText(currentUser.getEmail());
        } else {
            this.accessTokenDisplay.setText(R.string.undefined);
            this.userIdDisplay.setText(R.string.undefined);
            this.emailDisplay.setText(R.string.undefined);
        }
    }

    public void doLogout(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure to logout ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.doLogoutWithConfirmation();
                    }
                })
                .setNeutralButton("No", null)
                .show();
    }

    private void doLogoutWithConfirmation() {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging out...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Logout success")
                .setMessage("You have logged out.")
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Logout failed")
                .setMessage("")
                .create();

        this.skygear.logout(new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                loading.dismiss();
                successDialog.show();

                MainActivity.this.updateUserInfoDisplay();
            }

            @Override
            public void onLogoutFail(String reason) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + reason);
                failDialog.show();
            }
        });
    }

    public void goSignup(View v) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void goLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void goRecordCreateDelete(View view) {
        startActivity(new Intent(this, RecordCreateActivity.class));
    }

    public void goRecordQuery(View view) {
        startActivity(new Intent(this, RecordQueryActivity.class));
    }

    public void goPubsub(View view) {
        startActivity(new Intent(this, PubsubActivity.class));
    }

    public void goUserQuery(View view) {
        startActivity(new Intent(this, UserQueryActivity.class));
    }

    public void goLambda(View view) {
        startActivity(new Intent(this, LambdaActivity.class));
    }
}
