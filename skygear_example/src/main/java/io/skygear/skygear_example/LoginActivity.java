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
import io.skygear.skygear.Error;
import io.skygear.skygear.User;

public class LoginActivity extends AppCompatActivity {
    private static String LOG_TAG = LoginActivity.class.getSimpleName();

    private Container skygear;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailInput = (EditText) findViewById(R.id.email_input);
        this.passwordInput = (EditText) findViewById(R.id.password_input);

        this.skygear = Container.defaultContainer(this);
    }

    public void doLogin(View view) {
        String email = this.emailInput.getText().toString();
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

        Log.i(LOG_TAG, "doLogin: Signup with email: " + email);
        Log.i(LOG_TAG, "doLogin: Signup with password: " + password);

        this.skygear.auth().loginWithEmail(email, password, new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                loading.dismiss();
                successDialog.setMessage("Success with token:\n" + user.getAccessToken());
                successDialog.show();
            }

            @Override
            public void onAuthFail(Error error) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();
            }
        });
    }
}
