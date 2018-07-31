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

package io.skygear.skygear;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.skygear.skygear.RecordSerializer.RecordIdentifier;

/**
 * The Skygear Record Delete Request.
 */
public class RecordDeleteRequest extends Request {
    private static final String TAG = "Skygear SDK";

    private String databaseId;
    private List<RecordIdentifier> recordIdentifiers;

    /**
     * Instantiates a new record delete request with default properties.
     */
    RecordDeleteRequest() {
        super("record:delete");
        this.data = new HashMap<>();
        this.recordIdentifiers = new ArrayList<>();
    }

    /**
     * Instantiates a new record delete request.
     *
     * @param records  the records
     * @param database the database
     */
    public RecordDeleteRequest(Record[] records, Database database) {
        this();
        this.databaseId = database.getName();

        for (Record perRecord : records) {
            this.recordIdentifiers.add(
                    new RecordIdentifier(perRecord.type, perRecord.id)
            );
        }

        this.updateData();
    }

    /**
     * Instantiates a new record delete request.
     *
     * @param recordType the record type
     * @param recordIDs  the record IDs
     * @param database   the database
     */
    public RecordDeleteRequest(String recordType, String[] recordIDs, Database database) {
        this();
        this.databaseId = database.getName();

        for (String perRecordID: recordIDs) {
            this.recordIdentifiers.add(
                    new RecordIdentifier(recordType, perRecordID)
            );
        }

        this.updateData();
    }

    private void updateData() {
        JSONArray deprecatedIDs = new JSONArray();
        JSONArray recordIdentifiers = new JSONArray();

        try {
            for (RecordIdentifier perRecordIdentifier : this.recordIdentifiers) {
                String perRecordDeprecatedID = String.format(
                        "%s/%s",
                        perRecordIdentifier.type,
                        perRecordIdentifier.id
                );

                deprecatedIDs.put(perRecordDeprecatedID);

                JSONObject perRecordIdentifierData = new JSONObject();
                perRecordIdentifierData.put("_recordType", perRecordIdentifier.type);
                perRecordIdentifierData.put("_recordID", perRecordIdentifier.id);

                recordIdentifiers.put(perRecordIdentifierData);
            }
        } catch(JSONException e) {
            Log.w(TAG, "Fail to serialize record identifiers");
        }

        this.data.put("ids", deprecatedIDs);
        this.data.put("records", recordIdentifiers);
        this.data.put("database_id", this.databaseId);
    }

    @Override
    protected void validate() throws Exception {
        super.validate();

        JSONArray ids = (JSONArray) this.data.get("ids");
        if (ids.length() == 0) {
            throw new InvalidParameterException("No records to be processed");
        }
    }
}
