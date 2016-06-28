package io.skygear.skygear_example;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Map;

import io.skygear.skygear.Container;
import io.skygear.skygear.Query;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordDeleteResponseHandler;
import io.skygear.skygear.RecordQueryResponseHandler;

public class RecordQueryActivity extends AppCompatActivity {
    private static final String TAG = RecordQueryActivity.class.getSimpleName();

    private EditText[] recordKeyFields;
    private EditText[] recordValueFields;
    private Spinner[] operatorSpinners;

    private Container skygear;
    private Record[] records;
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_query);

        this.skygear = Container.defaultContainer(this);

        this.recordKeyFields = new EditText[] {
                (EditText) findViewById(R.id.record_key1),
                (EditText) findViewById(R.id.record_key2)
        };

        this.recordValueFields = new EditText[] {
                (EditText) findViewById(R.id.record_value1),
                (EditText) findViewById(R.id.record_value2)
        };

        this.operatorSpinners = new Spinner[] {
                (Spinner) findViewById(R.id.operator1),
                (Spinner) findViewById(R.id.operator2)
        };

        this.display = (TextView) findViewById(R.id.record_display);
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

    private void appendQueryPredicate(Query query, int operatorIndex, String key, String value) {
        switch (operatorIndex) {
            case 0:
                query.equalTo(key, value);
                break;
            case 1:
                query.like(key, value);
                break;
            default:
                Log.w(TAG, "addQueryPredicate: Unknown operator index " + operatorIndex, null);
        }
    }

    public void doQuery(View view) {
        this.dismissKeyboard();

        Query query = new Query("Demo");
        for (int idx = 0; idx < this.recordKeyFields.length; idx++) {
            String keyString = this.recordKeyFields[idx].getText().toString();
            String valueString = this.recordValueFields[idx].getText().toString();
            int operatorIndex = this.operatorSpinners[idx].getSelectedItemPosition();

            if (keyString.length() > 0) {
                this.appendQueryPredicate(query, operatorIndex, keyString, valueString);
            }
        }

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Query Success")
                .setMessage("")
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Query Fail")
                .setMessage("")
                .create();

        skygear.getPublicDatabase().query(query, new RecordQueryResponseHandler() {
            @Override
            public void onQuerySuccess(Record[] records) {
                RecordQueryActivity.this.records = records;
                RecordQueryActivity.this.updateRecordDisplay();

                successDialog.setMessage(
                        String.format("Successfully got %d records", records.length)
                );
                successDialog.show();
            }

            @Override
            public void onQueryError(String reason) {
                failDialog.setMessage(
                        String.format("Fail with reason:\n%s", reason)
                );

                failDialog.show();
            }
        });
    }

    public void doDelete(View view) {
        this.dismissKeyboard();

        if (this.records == null || this.records.length == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("No records")
                    .setMessage("No records selected. You may perform query first")
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm delete")
                    .setMessage(String.format("Are you sure to delete the %d records ?", this.records.length))
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RecordQueryActivity.this.doDeleteWithConfirm();
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
                RecordQueryActivity.this.records = null;
                RecordQueryActivity.this.updateRecordDisplay();

                successDialog.setMessage(
                        String.format("Successfully delete %d records", ids.length)
                );
                successDialog.show();
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                RecordQueryActivity.this.records = null;
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
