package io.skygear.skygear_example;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.skygear.skygear.Container;
import io.skygear.skygear.PubsubHandler;

public class PubsubActivity extends AppCompatActivity {
    private static final String TAG = PubsubActivity.class.getSimpleName();

    private Container skygear;

    private EditText channelNameEditText;
    private EditText messageEditText;
    private ScrollView displayScrollView;
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubsub);

        this.skygear = Container.defaultContainer(this);
        
        this.channelNameEditText = (EditText) findViewById(R.id.channel_name);
        this.messageEditText = (EditText) findViewById(R.id.message_text);

        this.displayScrollView = (ScrollView) findViewById(R.id.pubsub_display_scrollview);
        this.display = (TextView) findViewById(R.id.pubsub_display);
    }

    private void dismissKeyboard() {
        View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void addMessageToDisplay(String message) {
        String text = this.display.getText().toString();
        text += message + "\n\n";

        this.display.setText(text);

        final ScrollView scrollView = this.displayScrollView;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void clearDisplay() {
        this.display.setText("");
    }

    public void onSubscribeButtonClick(View view) {
        String channelName = this.channelNameEditText.getText().toString().trim();
        if (channelName.length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Channel Subscribe")
                    .setMessage("Cannot subscribe empty channel name")
                    .setNeutralButton("Dismiss", null)
                    .show();
            return;
        }

        this.addMessageToDisplay(String.format("Subscribe to \"%s\"", channelName));
        this.skygear.getPubsub().subscribe(channelName, new PubsubHandler() {
            @Override
            public void handle(JSONObject data) {
                String messageToDisplay;
                try {
                    messageToDisplay = data.toString(2);
                } catch (JSONException e) {
                    messageToDisplay = "Got invalid malformed JSON";
                }

                PubsubActivity.this.addMessageToDisplay(messageToDisplay);
            }
        });
    }

    public void onUnsubscribeButtonClick(View view) {
        String channelName = this.channelNameEditText.getText().toString().trim();
        if (channelName.length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Channel Unsubscribe")
                    .setMessage("Cannot unsubscribe empty channel name")
                    .setNeutralButton("Dismiss", null)
                    .show();
            return;
        }

        this.addMessageToDisplay(String.format("Unsubscribe to \"%s\"", channelName));
        this.skygear.getPubsub().unsubscribeAll(channelName);
    }

    public void onSendButtonClick(View view) {
        String channelName = this.channelNameEditText.getText().toString().trim();
        if (channelName.length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Publish")
                    .setMessage("Cannot publish to empty channel name")
                    .setNeutralButton("Dismiss", null)
                    .show();
            return;
        }

        String message = this.messageEditText.getText().toString().trim();
        if (message.length() > 0) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("message", message);
            } catch (JSONException e) {
                Log.w(TAG, "onSendButtonClick: Malformed JSON Object", e);
            }

            this.skygear.getPubsub().publish(channelName, jsonObject);
            this.messageEditText.setText("");
        }
    }
}
