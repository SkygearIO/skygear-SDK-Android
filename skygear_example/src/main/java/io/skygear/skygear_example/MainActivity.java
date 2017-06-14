package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Configuration;
import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.LogoutResponseHandler;
import io.skygear.skygear.Role;
import io.skygear.skygear.User;
import io.skygear.skygear.gcm.RegistrationIntentService;

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
        this.restoreServerConfiguration();

        if (this.skygear.getPush().getGcmSenderId() != null) {
            Intent gcmTokenRegisterIntent = new Intent(this, RegistrationIntentService.class);
            this.startService(gcmTokenRegisterIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Configuration skygearConfig = this.skygear.getConfig();

        this.endpointDisplay.setText(skygearConfig.getEndpoint());
        this.apiKeyDisplay.setText(skygearConfig.getApiKey());

        this.updateUserInfoDisplay();
    }

    private void restoreServerConfiguration() {
        ServerConfigurationPreference pref = new ServerConfigurationPreference(this);
        Configuration config = pref.get();

        if (config != null) {
            this.skygear.configure(config);
        }
    }

    private void updateUserInfoDisplay() {
        User currentUser = this.skygear.getAuth().getCurrentUser();
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

        this.skygear.getAuth().logout(new LogoutResponseHandler() {
            @Override
            public void onLogoutSuccess() {
                loading.dismiss();
                successDialog.show();

                MainActivity.this.updateUserInfoDisplay();
            }

            @Override
            public void onLogoutFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();
            }
        });
    }

    public void doGetCurrentUser(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Finding \"Who am I\"...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        this.skygear.getAuth().whoami(new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                loading.dismiss();

                StringBuffer buffer = new StringBuffer();
                buffer.append("Current user:\n");
                buffer.append(String.format("\tUser ID: %s\n", user.getId()));
                buffer.append(String.format("\tUsername: %s\n", user.getUsername()));
                buffer.append(String.format("\tEmail: %s\n", user.getEmail()));

                if (user.getLastLoginTime() != null) {
                    buffer.append(String.format(
                            "\tLast Login: %s\n",
                            user.getLastLoginTime().toString())
                    );
                }

                if (user.getLastSeenTime() != null) {
                    buffer.append(String.format(
                            "\tLast Seen: %s\n",
                            user.getLastSeenTime().toString())
                    );
                }

                if (user.getRoles().length > 0) {
                    buffer.append("\tRoles:\n");
                    for (Role perRole : user.getRoles()) {
                        buffer.append("\t\t").append(perRole.getName()).append("\n");
                    }
                }

                successDialog.setMessage(buffer.toString());
                successDialog.show();
            }

            @Override
            public void onAuthFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason:\n" + error.getMessage());
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

    public void goServerConfiguration(View view) {
        startActivity(new Intent(this, ServerConfigurationActivity.class));
    }
}
