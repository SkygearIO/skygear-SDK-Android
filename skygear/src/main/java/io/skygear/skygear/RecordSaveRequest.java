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

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Skygear Record Save Request.
 */
public class RecordSaveRequest extends Request {
    private String databaseId;
    private List<Record> records;

    /**
     * Instantiates a record save request with default properties.
     */
    RecordSaveRequest() {
        super("record:save");
        this.data = new HashMap<>();
        this.records = new ArrayList<>();
    }

    /**
     * Instantiates a record save request.
     *
     * @param records  the records
     * @param database the database
     */
    public RecordSaveRequest(Record[] records, Database database) {
        this();
        this.databaseId = database.getName();
        this.records = Arrays.asList(records);
        this.updateData();
    }

    /**
     * Instantiates a record save request.
     *
     * @param record   the record
     * @param database the database
     */
    public RecordSaveRequest(Record record, Database database) {
        this(new Record[]{ record }, database);
    }

    public void setAtomic(boolean isAtomic) {
        if (isAtomic) {
            this.data.put("atomic", true);
            return;
        }

        if (this.data.containsKey("atomic")) {
            this.data.remove("atomic");
        }
    }

    public boolean isAtomic() {
        if (!this.data.containsKey("atomic")) {
            return false;
        }

        return (boolean) this.data.get("atomic");
    }

    private void updateData() {
        JSONArray recordArray = new JSONArray();
        for (Record perRecord : this.records) {
            JSONObject perJsonObject = perRecord.toJson();
            recordArray.put(perJsonObject);
        }

        this.data.put("records", recordArray);
        this.data.put("database_id", this.databaseId);
        this.data.put("atomic", true);
    }

    @Override
    protected void validate() throws Exception {
        super.validate();

        JSONArray records = (JSONArray)this.data.get("records");
        if (records.length() == 0) {
            throw new InvalidParameterException("No records to be processed");
        }

        for (Record perRecord : this.records) {
            for (Object perRecordPerValue : perRecord.getData().values()) {
                if (perRecordPerValue instanceof Asset && ((Asset) perRecordPerValue).isPendingUpload()) {
                    throw new InvalidParameterException("Cannot save records with pending upload asset");
                }
            }
        }
    }
}
