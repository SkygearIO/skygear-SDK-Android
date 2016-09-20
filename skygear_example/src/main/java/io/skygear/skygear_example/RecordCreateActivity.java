package io.skygear.skygear_example;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import io.skygear.skygear.Asset;
import io.skygear.skygear.AssetPostRequest;
import io.skygear.skygear.Container;
import io.skygear.skygear.Record;
import io.skygear.skygear.RecordDeleteResponseHandler;
import io.skygear.skygear.RecordSaveResponseHandler;

public class RecordCreateActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQ = 12345;

    private EditText[] recordKeyFields;
    private EditText[] recordValueFields;

    private EditText recordAssetKeyField;
    private ImageView recordAssetImageView;
    private Button recordAssetButton;

    private TextView display;
    private Button deleteButton;

    private Container skygear;
    private Record record;
    private Asset recordAsset;

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

        this.recordAssetKeyField = (EditText) findViewById(R.id.record_asset_key);
        this.recordAssetImageView = (ImageView) findViewById(R.id.record_asset_image_view);
        this.recordAssetButton = (Button) findViewById(R.id.record_asset_button);

        this.deleteButton = (Button) findViewById(R.id.delete_button);
        this.display = (TextView) findViewById(R.id.record_display);

        this.updateRecordDisplay();
        this.updateAssetViews();
    }

    private void updateRecordDisplay() {
        String displayText;
        if (this.record == null) {
            displayText = "No records";
            this.deleteButton.setEnabled(false);
        } else {
            try {
                displayText = String.format(
                        "Created record:\n\n%s",
                        this.record.toJson().toString(2)
                );
                this.deleteButton.setEnabled(true);

            } catch (JSONException e) {
                displayText = "Invalid JSON format";
                this.deleteButton.setEnabled(false);
            }
        }

        this.display.setText(displayText);
    }

    private void updateAssetViews() {
        if (this.recordAsset == null) {
            this.recordAssetImageView.setImageDrawable(null);
            this.recordAssetButton.setText(R.string.set_image);
            this.recordAssetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordCreateActivity.this.doPickImage(v);
                }
            });

            return;
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                this.recordAsset.getData(),
                0,
                (int) this.recordAsset.getSize()
        );
        this.recordAssetImageView.setImageDrawable(new BitmapDrawable(null, bitmap));
        this.recordAssetButton.setText(R.string.remove_image);
        this.recordAssetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordCreateActivity.this.doRemoveImage(v);
            }
        });
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

        String recordAssetKey = this.recordAssetKeyField.getText().toString().trim();
        if (recordAssetKey.length() > 0 && this.recordAsset != null) {
            newRecord.set(recordAssetKey, this.recordAsset);
        }

        final AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle("Save Success")
                .setMessage("Successfully saved")
                .create();

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Save Fail")
                .setMessage("")
                .create();

        skygear.getPublicDatabase().save(newRecord, new RecordSaveResponseHandler(){
            @Override
            public void onSaveSuccess(Record[] records) {
                RecordCreateActivity.this.record = records[0];
                RecordCreateActivity.this.updateRecordDisplay();

                successDialog.show();
            }

            @Override
            public void onPartiallySaveSuccess(Map<String, Record> successRecords, Map<String, String> reasons) {
                failDialog.setMessage("Unexpected Error");
                failDialog.show();
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

        if (this.record == null) {
            new AlertDialog.Builder(this)
                    .setTitle("No records")
                    .setMessage("No records selected. You may create one first")
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm delete")
                    .setMessage("Are you sure to delete the record?")
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

        final AlertDialog failDialog = new AlertDialog.Builder(this)
                .setTitle("Delete Fail")
                .setMessage("")
                .create();

        skygear.getPublicDatabase().delete(this.record, new RecordDeleteResponseHandler() {
            @Override
            public void onDeleteSuccess(String[] ids) {
                RecordCreateActivity.this.record = null;
                RecordCreateActivity.this.updateRecordDisplay();

                successDialog.setMessage("Successfully delete the record");
                successDialog.show();
            }

            @Override
            public void onDeletePartialSuccess(String[] ids, Map<String, String> reasons) {
                failDialog.setMessage("Unexpected Error");
                failDialog.show();
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

    public void doRemoveImage(View view) {
        this.recordAsset = null;
        this.updateAssetViews();
    }

    public void doPickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_IMAGE_REQ:
                if (resultCode == RESULT_OK) {
                    this.handleImagePick(data.getData());


                }
        }
    }

    private void handleImagePick(Uri uri) {
        Log.i("RecordCreateActivity", "handleImagePick: Got URI: " + uri);
        ContentResolver contentResolver = getContentResolver();

        try {
            final InputStream inputStream = contentResolver.openInputStream(uri);
            final String mimeType = contentResolver.getType(uri);
            Log.i("RecordCreateActivity", "handleImagePick: Got MIME-Type: " + mimeType);

            final ProgressDialog loading = new ProgressDialog(this);
            loading.setTitle("Loading");
            loading.setMessage("Decoding image...");
            loading.setCancelable(false);
            loading.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("RecordCreateActivity", "handleImagePick: Start decoding the image");

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                    final byte[] bytes = byteArrayOutputStream.toByteArray();
                    Log.i("RecordCreateActivity", "handleImagePick: Finish decoding, size: " + bytes.length);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loading.dismiss();
                            RecordCreateActivity.this.handleImageUpload(bytes, mimeType);
                        }
                    });
                }
            }).start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleImageUpload(byte[] data, String mimeType) {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setTitle("Loading");
        loading.setMessage("Uploading image...");
        loading.setCancelable(false);
        loading.show();

        Asset asset = new Asset("Record-Image", mimeType, data);
        this.skygear.uploadAsset(asset, new AssetPostRequest.ResponseHandler() {
            @Override
            public void onPostSuccess(Asset asset, String response) {
                Log.i("RecordCreateActivity", "handleImageUpload: successfully uploaded to " + asset.getUrl());
                RecordCreateActivity.this.recordAsset = asset;
                RecordCreateActivity.this.updateAssetViews();
                loading.dismiss();
            }

            @Override
            public void onPostFail(Asset asset, String reason) {
                Log.i("RecordCreateActivity", "handleImageUpload: fail - " + reason);
                loading.dismiss();
            }
        });
    }

}
