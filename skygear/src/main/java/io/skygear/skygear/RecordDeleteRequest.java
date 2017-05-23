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
        JSONArray recordArray = new JSONArray();
        for (Record perRecord : this.records) {
            recordArray.put(String.format("%s/%s", perRecord.getType(), perRecord.getId()));
        }

        this.data.put("ids", recordArray);
        this.data.put("database_id", this.databaseId);
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
            typeSet.add(perRecord.type);
        }

        if (typeSet.size() > 1) {
            throw new InvalidParameterException("Only records in the same type are allowed");
        }
    }
}
