package io.skygear.skygear_example;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Map;

import io.skygear.skygear.Container;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordDeleteResponseHandler;
import io.skygear.skygear.RecordSaveResponseHandler;

public class RecordCreateActivity extends AppCompatActivity {
    private EditText[] recordKeyFields;
    private EditText[] recordValueFields;

    private Container skygear;
    private Record[] records;
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_create);

        this.skygear = Container.defaultContainer(this);

        this.recordKeyFields = new EditText[] {
                (EditText) findViewById(R.id.record_key1),
                (EditText) findViewById(R.id.record_key2)
        };

        this.recordValueFields = new EditText[] {
                (EditText) findViewById(R.id.record_value1),
                (EditText) findViewById(R.id.record_value2)
        };

        this.display = (TextView) findViewById(R.id.record_display);
        this.updateRecordDisplay();
    }

    private void updateRecordDisplay() {
        String displayText;
        if (this.records == null || this.records.length == 0) {
            displayText = "No records";

        } else {
            StringBuffer buffer = new StringBuffer();
            buffer.append(String.format("Got %d records\n\n", this.records.length));

            try {
                for (int idx = 0; idx < this.records.length; idx++) {
                    buffer.append(String.format("Record[%d]:\n", idx))
                            .append(this.records[idx].toJson().toString(2))
                            .append("\n\n");
                }
                displayText = buffer.toString();

            } catch (JSONException e) {
                displayText = "Invalid JSON format";
            }
        }

        this.display.setText(displayText);
    }

    private void dismissKeyboard() {
        View currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public void doSave(View view) {
        this.dismissKeyboard();

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
                RecordCreateActivity.this.records = records;
                RecordCreateActivity.this.updateRecordDisplay();

                successDialog.setMessage(
                        String.format("Successfully saved %d records", records.length)
                );
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

    public void doDelete(View view) {
        this.dismissKeyboard();

        if (this.records == null || this.records.length == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("No records")
                    .setMessage("No records selected. You may create one first")
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm delete")
                    .setMessage(String.format("Are you sure to delete the %d records ?", this.records.length))
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecordCreateActivity.this.doDeleteWithConfirm();
                        }
                    })
                    .show();
        }
    }

    public void doDeleteWithConfirm() {
        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Success")
                .setMessage("")
                .create();

        final AlertDialog partiallySuccessDialog = new AlertDialog.Builder(this)
                .setTitle("Some Records Delete Success")
                .setMessage("")
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Fail")
                .setMessage("")
                .create();

        skygear.getPublicDatabase().delete(this.records, new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                RecordCreateActivity.this.records = null;
                RecordCreateActivity.this.updateRecordDisplay();

                successDialog.setMessage(
                        String.format("Successfully delete %d records", ids.length)
                );
                successDialog.show();
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                RecordCreateActivity.this.records = null;
                partiallySuccessDialog.setMessage(
                        String.format("%d successes\n%d fails", ids.length, reasons.size())
                );
                partiallySuccessDialog.show();
            }

            @Override
            public void onDeleteFail(String reason) {
                failDialog.setMessage(
                        String.format("Fail with reason:\n%s", reason)
                );
                failDialog.show();
            }
        });
    }
}