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

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RecordQueryRequestUnitTest {
    static Context instrumentationContext;
    static Container instrumentationContainer;
    static Database instrumentationPublicDatabase;

    @BeforeClass
    public static void setUpClass() throws Exception {
        instrumentationContext = InstrumentationRegistry.getContext().getApplicationContext();
        instrumentationContainer = new Container(instrumentationContext, Configuration.testConfiguration());
        instrumentationPublicDatabase = Database.Factory.publicDatabase(instrumentationContainer);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instrumentationContext = null;
        instrumentationContainer = null;
        instrumentationPublicDatabase = null;
    }

    @Test
    public void testRecordQueryRequestNormalFlow() throws Exception {
        Query query = new Query("Note")
                .equalTo("title", "Hello World")
                .addDescending("rating");

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        Map<String, Object> data = request.data;

        assertEquals("_public", data.get("database_id"));
        assertEquals("Note", data.get("record_type"));

        JSONArray predicate = (JSONArray) data.get("predicate");
        assertEquals("eq", predicate.getString(0));

        JSONObject predicateKeypath = predicate.getJSONObject(1);
        assertEquals("keypath", predicateKeypath.getString("$type"));
        assertEquals("title", predicateKeypath.getString("$val"));

        assertEquals("Hello World", predicate.getString(2));

        JSONArray sortPredicate = (JSONArray) data.get("sort");
        JSONArray sortPredicate1 = sortPredicate.getJSONArray(0);

        JSONObject sortPredicate1Keypath = sortPredicate1.getJSONObject(0);
        assertEquals("keypath", sortPredicate1Keypath.getString("$type"));
        assertEquals("rating", sortPredicate1Keypath.getString("$val"));

        assertEquals("desc", sortPredicate1.getString(1));
    }

    @Test
    public void testRecordQueryLimit() throws Exception {
        Query query = new Query("Note");
        query.setLimit(20);

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        assertEquals(20, request.data.get("limit"));
    }

    @Test
    public void testRecordQueryDefaultLimit() throws Exception {
        Query query = new Query("Note");

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        assertEquals(50, request.data.get("limit"));
    }

    @Test
    public void testRecordQueryOffset() throws Exception {
        Query query = new Query("Note");
        query.setOffset(25);

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        assertEquals(25, request.data.get("offset"));
    }

    @Test
    public void testRecordQueryDefaultOffset() throws Exception {
        Query query = new Query("Note");

        RecordQueryRequest request = new RecordQueryRequest(query, instrumentationPublicDatabase);
        assertEquals(0, request.data.get("offset"));
    }
}
