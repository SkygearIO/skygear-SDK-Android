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

    private void updateData() {
        JSONArray recordArray = new JSONArray();
        for (Record perRecord : this.records) {
            JSONObject perJsonObject = perRecord.toJson();
            recordArray.put(perJsonObject);
        }

        this.data.put("records", recordArray);
        this.data.put("database_id", this.databaseId);
    }

    @Override
    protected void validate() throws Exception {
        super.validate();

        JSONArray records = (JSONArray)this.data.get("records");
        if (records.length() == 0) {
            throw new InvalidParameterException("No records to be processed");
        }

        Set<String> typeSet = new HashSet<>();
        for (Record perRecord : this.records) {
            typeSet.add(perRecord.type);

            for (Object perRecordPerValue : perRecord.getData().values()) {
                if (perRecordPerValue instanceof Asset && ((Asset) perRecordPerValue).isPendingUpload()) {
                    throw new InvalidParameterException("Cannot save records with pending upload asset");
                }
            }
        }

        if (typeSet.size() > 1) {
            throw new InvalidParameterException("Only records in the same type are allowed");
        }
    }
}
