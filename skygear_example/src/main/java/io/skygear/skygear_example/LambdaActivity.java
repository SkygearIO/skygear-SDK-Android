package io.skygear.skygear_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.LambdaResponseHandler;

public class LambdaActivity extends AppCompatActivity {

    private Container skygear;
    private EditText functionNameEditText;
    private EditText[] functionArgumentEditTexts;
    private TextView display;
    private ScrollView displayScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lambda);

        this.skygear = Container.defaultContainer(this);

        this.display = (TextView) findViewById(R.id.lambda_display);
        this.displayScrollView = (ScrollView) findViewById(R.id.lambda_display_scrollview);
        this.functionNameEditText = (EditText) findViewById(R.id.lambda_function_name_edittext);
        this.functionArgumentEditTexts = new EditText[]{
                (EditText) findViewById(R.id.lambda_function_arg1_edittext),
                (EditText) findViewById(R.id.lambda_function_arg2_edittext),
                (EditText) findViewById(R.id.lambda_function_arg3_edittext)
        };
    }

    private void display(String displayString) {
        String currentDisplayString = this.display.getText().toString();
        currentDisplayString += displayString + "\n";

        this.display.setText(currentDisplayString);

        final ScrollView scrollView = this.displayScrollView;
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public void doSendLambdaFunction(View view) {
        String functionName = this.functionNameEditText.getText().toString().trim();
        if (functionName.length() == 0) {
            return;
        }

        List<String> argList = new LinkedList<>();
        for (EditText perEditText : functionArgumentEditTexts) {
            String perArgument = perEditText.getText().toString().trim();
            if (perArgument.length() == 0) {
                break;
            }

            argList.add(perArgument);
        }

        String[] argv = null;
        int argc = argList.size();
        if (argc > 0) {
            argv = new String[argc];
            argList.toArray(argv);
        }

        StringBuilder buff = new StringBuilder();
        buff.append("Calling ").append(functionName).append("(");

        for (int idx = 0; idx < argc; idx++) {
            buff.append(argList.get(idx));
            if (idx + 1 != argc) {
                buff.append(", ");
            }
        }

        buff.append(")...");
        this.display(buff.toString());

        this.skygear.callLambdaFunction(functionName, argv, new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                String displayString;
                try {
                    displayString = "Result:\n" + result.toString(2) + "\n";
                } catch (JSONException e) {
                    displayString = "Error: Got malformed JSON Object\n" ;
                }

                LambdaActivity.this.display(displayString);
            }

            @Override
            public void onLambdaFail(Error error) {
                LambdaActivity.this.display("Error: " + error.getMessage() + "\n");
            }
        });
    }
}
