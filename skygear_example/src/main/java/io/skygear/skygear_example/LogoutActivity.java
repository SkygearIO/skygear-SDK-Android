package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import io.skygear.skygear.AuthResponseHandler;
import io.skygear.skygear.Container;
import io.skygear.skygear.LogoutResponseHandler;

public class LogoutActivity extends AppCompatActivity {
    private static String LOG_TAG = LogoutActivity.class.getSimpleName();

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        this.skygear = Container.defaultContainer(this);
    }

    public void doLogout(View view) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Logging out...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Logout success")
                .setMessage("")
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
            }

            @Override
            public void onLogoutFail(String reason) {
                loading.dismiss();

                failDialog.setMessage("Fail with reason: \n" + reason);
                failDialog.show();
            }
        });
    }
}
