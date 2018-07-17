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

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class ReferenceSerializerUnitTest {
    @Test
    public void testReferenceSerializationNormalFlow() throws Exception {
        Reference ref = new Reference("Note", "c71c6ce4-a3c7-4b7d-833b-46b7df8cec03");
        JSONObject jsonObject = ReferenceSerializer.serialize(ref);

        assertEquals("ref", jsonObject.getString("$type"));
        assertEquals("Note/c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", jsonObject.getString("$id"));
        assertEquals("Note", jsonObject.getString("$recordType"));
        assertEquals("c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", jsonObject.getString("$recordID"));
    }

    @Test
    public void testReferenceDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "ref");
        jsonObject.put("$recordType", "Note");
        jsonObject.put("$recordID", "c71c6ce4-a3c7-4b7d-833b-46b7df8cec03");

        Reference ref = ReferenceSerializer.deserialize(jsonObject);
        assertEquals("Note", ref.getType());
        assertEquals("c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", ref.getId());
    }

    @Test
    public void testReferenceDeserializationDeprecatedFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "ref");
        jsonObject.put("$id", "Note/c71c6ce4-a3c7-4b7d-833b-46b7df8cec03");

        Reference ref = ReferenceSerializer.deserialize(jsonObject);
        assertEquals("Note", ref.getType());
        assertEquals("c71c6ce4-a3c7-4b7d-833b-46b7df8cec03", ref.getId());
    }

    @Test
    public void testReferenceFormatChecking() throws Exception {
        assertFalse(ReferenceSerializer.isReferenceFormat(null));
        assertFalse(ReferenceSerializer.isReferenceFormat(new Object()));
        assertFalse(ReferenceSerializer.isReferenceFormat(new JSONObject("{}")));
        assertFalse(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{\"$type\": \"record\"}")
        ));
        assertFalse(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{\"$type\": \"record\", \"$recordType\": \"Note\"}")
        ));
        assertFalse(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{\"$type\": \"record\", \"$recordID\": \"some-id\"}")
        ));
        assertFalse(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{\"$type\": \"record\", \"$id\": \"some-id\"}")
        ));
        assertTrue(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{" +
                        "\"$type\": \"ref\", " +
                        "\"$recordType\": \"some-id\", " +
                        "\"$recordID\": \"some-id\"" +
                    "}")
        ));
        assertTrue(ReferenceSerializer.isReferenceFormat(
                new JSONObject("{\"$type\": \"ref\", \"$id\": \"Note/some-id\"}")
        ));
    }
}
