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

import java.util.HashMap;

/**
 * The Skygear Record Query Request.
 */
public class RecordQueryRequest extends Request {

    private Query query;
    private final String databaseId;

    /**
     * Instantiates a new record query request.
     *
     * @param query    the query
     * @param database the database
     */
    public RecordQueryRequest(Query query, Database database) {
        super("record:query");

        this.databaseId = database.getName();
        this.data = new HashMap<>();

        this.setQuery(query);
        this.updateData();
    }

    /**
     * Instantiates a new record query request.
     *
     * @param database the database
     */
    public RecordQueryRequest(Database database) {
        this(null, database);
    }

    public Query getQuery() {
        return this.query;
    }

    public void setQuery(Query query) {
        this.query = query;

        if (query == null) {
            this.data.remove("record_type");
            this.data.remove("sort");
            this.data.remove("predicate");
            this.data.remove("include");
            this.data.remove("limit");
            this.data.remove("count");
            this.data.remove("offset");

            return;
        }

        this.data.put("record_type", this.query.getType());
        this.data.put("sort", this.query.getSortPredicateJson());

        JSONArray predicateJson = this.query.getPredicateJson();
        if (predicateJson.length() > 0) {
            this.data.put("predicate", predicateJson);
        }

        JSONObject transientPredicateJson = this.query.getTransientPredicateJson();
        if (transientPredicateJson.length() > 0) {
            this.data.put("include", transientPredicateJson);
        }

        this.data.put("limit",  this.query.getLimit());
        this.data.put("count",  this.query.getOverallCount());
        this.data.put("offset", this.query.getOffset());
    }

    private void updateData() {
        this.data.put("database_id", this.databaseId);
    }
}
