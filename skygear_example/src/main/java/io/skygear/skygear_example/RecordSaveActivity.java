package io.skygear.skygear_example;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.skygear.skygear.Container;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordSaveResponseHandler;
import io.skygear.skygear.Request;

public class RecordSaveActivity extends AppCompatActivity {
    private static final String LOG_TAG = RecordSaveActivity.class.getSimpleName();

    private EditText[] recordKeyFields;
    private EditText[] recordValueFields;

    private Container skygear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_save);

        this.skygear = Container.defaultContainer(this);

        this.recordKeyFields = new EditText[] {
                (EditText) findViewById(R.id.record_key1),
                (EditText) findViewById(R.id.record_key2),
                (EditText) findViewById(R.id.record_key3)
        };

        this.recordValueFields = new EditText[] {
                (EditText) findViewById(R.id.record_value1),
                (EditText) findViewById(R.id.record_value2),
                (EditText) findViewById(R.id.record_value3)
        };
    }

    public void doSave(View view) {
        Record newRecord = new Record("Demo");
        for (int idx = 0; idx < this.recordKeyFields.length; idx++) {
            String keyString = this.recordKeyFields[idx].getText().toString();
            String valueString = this.recordValueFields[idx].getText().toString();

            if (keyString.length() > 0) {
                newRecord.set(keyString, valueString);
            }
        }

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Save Success")
                .setMessage("")
                .create();

        final AlertDialog partiallySuccessDialog = new AlertDialog.Builder(this)
                .setTitle("Some Records Save Success")
                .setMessage("")
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Save Fail")
                .setMessage("")
                .create();

        skygear.getPublicDatabase().save(newRecord, new RecordSaveResponseHandler(){
            @Override
            public void onSaveSuccess(Record[] records) {
                successDialog.show();
            }

            @Override
            public void onPartiallySaveSuccess(Map<String, Record> successRecords, Map<String, String> reasons) {
                partiallySuccessDialog.setMessage(
                        String.format("%d successes\n%d fails", successRecords.size(), reasons.size())
                );
                partiallySuccessDialog.show();
            }

            @Override
            public void onSaveFail(String reason) {
                failDialog.setMessage(String.format("Fail with reason:\n%s", reason));
                failDialog.show();
            }
        });
    }
}
