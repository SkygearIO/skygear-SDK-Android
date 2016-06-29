package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.User;

public class LoginActivity extends AppCompatActivity {
    private static String LOG_TAG = LoginActivity.class.getSimpleName();

    private Container skygear;
    private EditText usernameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.usernameInput = (EditText) findViewById(R.id.username_input);
        this.passwordInput = (EditText) findViewById(R.id.password_input);

        this.skygear = Container.defaultContainer(this);
    }

    public void doLogin(View view) {
        String username = this.usernameInput.getText().toString();
        String password = this.passwordInput.getText().toString();

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging in...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Login success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.this.finish();
                    }
                })
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Login failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        Log.i(LOG_TAG, "doLogin: Signup with username: " + username);
        Log.i(LOG_TAG, "doLogin: Signup with password: " + password);

        this.skygear.loginWithUsername(username, password, new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                loading.dismiss();
                successDialog.setMessage("Success with token:\n" + user.accessToken);
                successDialog.show();
            }

            @Override
            public void onAuthFail(String reason) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + reason);
                failDialog.show();
            }
        });
    }
}
