/*
 * Copyright 2017 Oursky Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.skygear.skygear_example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.skygear.skygear.Container;
import io.skygear.skygear.Error;
import io.skygear.skygear.LambdaResponseHandler;

public class LambdaWithMapActivity extends AppCompatActivity {

    private Container skygear;
    private EditText functionNameEditText;
    private EditText[] functionKeyEditTexts;
    private EditText[] functionValueEditTexts;
    private TextView display;
    private ScrollView displayScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lambda_with_map);

        this.skygear = Container.defaultContainer(this);

        this.display = (TextView) findViewById(R.id.lambda_display);
        this.displayScrollView = (ScrollView) findViewById(R.id.lambda_display_scrollview);
        this.functionNameEditText = (EditText) findViewById(R.id.lambda_function_name_edittext);
        this.functionKeyEditTexts = new EditText[]{
                (EditText) findViewById(R.id.lambda_function_key1_edittext),
                (EditText) findViewById(R.id.lambda_function_key2_edittext),
                (EditText) findViewById(R.id.lambda_function_key3_edittext)
        };
        this.functionValueEditTexts = new EditText[]{
                (EditText) findViewById(R.id.lambda_function_value1_edittext),
                (EditText) findViewById(R.id.lambda_function_value2_edittext),
                (EditText) findViewById(R.id.lambda_function_value3_edittext)
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

        HashMap<String, Object> data = new HashMap<String, Object>();
        for (int i = 0; i < functionKeyEditTexts.length; i++) {
            String perKey = functionKeyEditTexts[i].getText().toString().trim();
            String perValue = functionValueEditTexts[i].getText().toString().trim();
            if (perKey.length() == 0) {
                break;
            }
            data.put(perKey, perValue);
        }

        StringBuilder buff = new StringBuilder();
        buff.append("Calling ").append(functionName).append("(");

        int dc = data.size();
        int count = 0;
        for (String key : data.keySet()) {
            buff.append(key + "=" + data.get(key));
            if (count + 1 != dc) {
                buff.append(", ");
            }
            count++;
        }

        buff.append(")...");
        this.display(buff.toString());

        this.skygear.callLambdaFunction(functionName, data, new LambdaResponseHandler() {
            @Override
            public void onLambdaSuccess(JSONObject result) {
                String displayString;
                try {
                    displayString = "Result:\n" + result.toString(2) + "\n";
                } catch (JSONException e) {
                    displayString = "Error: Got malformed JSON Object\n" ;
                }

                LambdaWithMapActivity.this.display(displayString);
            }

            @Override
            public void onLambdaFail(Error error) {
                LambdaWithMapActivity.this.display("Error: " + error.getMessage() + "\n");
            }
        });
    }
}
