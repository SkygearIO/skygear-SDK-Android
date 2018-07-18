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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Skygear Record Delete Request.
 */
public class RecordDeleteRequest extends Request {
    private String databaseId;
    private List<Record> records;

    /**
     * Instantiates a new record delete request with default properties.
     */
    RecordDeleteRequest() {
        super("record:delete");
        this.data = new HashMap<>();
        this.records = new ArrayList<>();
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
        this.records = Arrays.asList(records);
        this.updateData();
    }

    private void updateData() {
        String recordType = null;
        JSONArray recordIDs = new JSONArray();
        JSONArray deprecatedIDs = new JSONArray();
        for (Record perRecord : this.records) {
            if (recordType == null) {
                recordType = perRecord.getType();
            }

            recordIDs.put(perRecord.getId());
            deprecatedIDs.put(String.format("%s/%s", perRecord.getType(), perRecord.getId()));
        }

        this.data.put("ids", deprecatedIDs);
        this.data.put("recordIDs", recordIDs);
        this.data.put("database_id", this.databaseId);

        if (recordType != null) {
            this.data.put("recordType", recordType);
        }
    }

    @Override
    protected void validate() throws Exception {
        super.validate();

        JSONArray ids = (JSONArray) this.data.get("ids");
        if (ids.length() == 0) {
            throw new InvalidParameterException("No records to be processed");
        }

        Set<String> typeSet = new HashSet<>();
        for (Record perRecord : this.records) {
            typeSet.add(perRecord.getType());
        }

        if (typeSet.size() > 1) {
            throw new InvalidParameterException("Only records in the same type are allowed");
        }
    }
}
