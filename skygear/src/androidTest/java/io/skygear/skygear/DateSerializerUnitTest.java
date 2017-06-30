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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DateSerializerUnitTest {
    @Test
    public void testDateSerializationNormalFlow() throws Exception {
        JSONObject jsonObject = DateSerializer.serialize(
                new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate()
        );

        assertEquals("date", jsonObject.getString("$type"));
        assertEquals("2016-06-15T07:55:34.342Z", jsonObject.getString("$date"));
    }

    @Test
    public void testDateDeserializationNormalFlow() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("$type", "date");
        jsonObject.put("$date", "2016-06-15T07:55:34.342Z");

        assertEquals(
                new DateTime(2016, 6, 15, 7, 55, 34, 342, DateTimeZone.UTC).toDate(),
                DateSerializer.deserialize(jsonObject)
        );
    }

    @Test
    public void testDateFromString() throws Exception {
        String dateStringWithMS = "2017-03-08T20:10:05.123Z";
        Date dateWithMS = DateSerializer.dateFromString(dateStringWithMS);

        assertEquals(
                new DateTime(2017, 3, 8, 20, 10, 5, 123, DateTimeZone.UTC).toDate(),
                dateWithMS
        );

        String dateStringWithoutMS = "2017-03-08T20:10:05Z";
        Date dateWithoutMS = DateSerializer.dateFromString(dateStringWithoutMS);

        assertEquals(
                new DateTime(2017, 3, 8, 20, 10, 5, 0, DateTimeZone.UTC).toDate(),
                dateWithoutMS
        );
    }

    @Test
    public void testStringFromDate() throws Exception {
        Date dateWithMS = new DateTime(2017, 3, 8, 20, 10, 5, 123, DateTimeZone.UTC).toDate();
        String dateStringWithMS = "2017-03-08T20:10:05.123Z";

        assertEquals(
                dateStringWithMS,
                DateSerializer.stringFromDate(dateWithMS)
        );

        Date dateWithZeroMS = new DateTime(2017, 3, 8, 20, 10, 5, 0, DateTimeZone.UTC).toDate();
        String dateStringWithZeroZeroZeroMS = "2017-03-08T20:10:05.000Z";

        assertEquals(
                dateStringWithZeroZeroZeroMS,
                DateSerializer.stringFromDate(dateWithZeroMS)
        );
    }
}