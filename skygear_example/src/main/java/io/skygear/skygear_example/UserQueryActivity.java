package io.skygear.skygear_example;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.User;
import io.skygear.skygear.UserQueryResponseHandler;

public class UserQueryActivity extends AppCompatActivity {

    private EditText userEmailEditText;
    private TextView display;

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_query);

        this.userEmailEditText = (EditText) findViewById(R.id.user_email_edit_text);
        this.display = (TextView) findViewById(R.id.query_display);

        this.skygear = Container.defaultContainer(this);
    }

    private void display(String displayString) {
        this.display.setText(displayString);
    }

    @SuppressLint("DefaultLocale")
    public void doQuery(View view) {
        String email = this.userEmailEditText.getText().toString();
        if (email.length() == 0) {
            return;
        }

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Querying user...");
        loading.show();

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Query success")
                .setMessage("")
                .setPositiveButton("Dismiss", null)
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Query failed")
                .setMessage("")
                .setNeutralButton("Dismiss", null)
                .create();

        this.skygear.auth().getUserByEmail(email, new UserQueryResponseHandler() {

            @Override
            public void onQuerySuccess(User[] users) {
                loading.dismiss();

                String message = "Cannot find any users";
                if (users.length > 0) {
                    message = "Successfully find the user";
                }

                successDialog.setMessage(message);
                successDialog.show();

                StringBuffer buffer = new StringBuffer();

                for (User perUser : users) {
                    buffer.append(String.format(
                            "ID: %s\n",
                            perUser.getId()
                    )).append(String.format(
                            "Email: %s\n",
                            perUser.getEmail()
                    )).append("\n");
                }

                UserQueryActivity.this.display(buffer.toString());
            }

            @Override
            public void onQueryFail(Error error) {
                loading.dismiss();
                failDialog.setMessage("Fail with reason: \n" + error.getMessage());
                failDialog.show();

                UserQueryActivity.this.display("");
            }
        });
    }
}
