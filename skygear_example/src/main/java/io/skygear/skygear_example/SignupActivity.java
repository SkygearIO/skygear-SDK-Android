package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.User;

public class SignupActivity extends AppCompatActivity {
    private static String LOG_TAG = SignupActivity.class.getSimpleName();

    private Container skygear;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        this.emailInput = (EditText) findViewById(R.id.email_input);
        this.passwordInput = (EditText) findViewById(R.id.password_input);

        this.skygear = Container.defaultContainer(this);
    }

    public void doSignup(View v) {
        String email = this.emailInput.getText().toString();
        String password = this.passwordInput.getText().toString();

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Signing up...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Signup success")
                .setMessage("")
                .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SignupActivity.this.finish();
                    }
                })
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Signup failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        Log.i(LOG_TAG, "doSignup: Signup with email: " + email);
        Log.i(LOG_TAG, "doSignup: Signup with password: " + password);

        this.skygear.signupWithEmail(email, password, new AuthResponseHandler() {
            @Override
            public void onAuthSuccess(User user) {
                loading.dismiss();
                successDialog.setMessage("Success with token:\n" + user.getAccessToken());
                successDialog.show();

                Log.i(LOG_TAG, "onAuthSuccess: Got token: " + user.getAccessToken());
            }

            @Override
            public void onAuthFail(String reason) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + reason);
                failDialog.show();

                Log.i(LOG_TAG, "onAuthFail: Fail with reason: " + reason);
            }
        });
    }
}
