package io.skygear.skygear;

import android.support.test.runner.AndroidJUnit4;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

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
}