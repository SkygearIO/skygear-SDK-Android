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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * The Skygear Date Serializer.
 *
 * This class converts between date object and JSON object in Skygear defined format.
 */
public class DateSerializer {
    private static DateTimeFormatter formatterWithMS = ISODateTimeFormat.dateTime().withZoneUTC();
    private static DateTimeFormatter formatterWithoutMS = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();

    /**
     * Serialize a date object.
     *
     * @param date the date object
     * @return the json object
     */
    public static JSONObject serialize(Date date) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$type", "date");
            jsonObject.put("$date", DateSerializer.stringFromDate(date));

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize a date object from json object.
     *
     * @param dateJsonObject the date json object
     * @return the date object
     * @throws JSONException the json exception
     */
    public static Date deserialize(JSONObject dateJsonObject) throws JSONException {
        String typeValue = dateJsonObject.getString("$type");
        if (typeValue.equals("date")) {
            String dateString = dateJsonObject.getString("$date");

            return DateSerializer.dateFromString(dateString);
        }

        throw new JSONException("Invalid $type value: " + typeValue);
    }

    /**
     * Determines whether an object is a JSON object in Skygear defined date format.
     *
     * @param object the object
     * @return the indicating boolean
     */
    public static boolean isDateFormat(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;

            return jsonObject.getString("$type").equals("date") &&
                    !jsonObject.isNull("$date");
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Parse a date string to a Date object
     * @param dateString the string representation of a date
     * @return the date object
     */
    static Date dateFromString(String dateString) {
        try {
            return DateSerializer.formatterWithMS.parseDateTime(dateString).toDate();
        } catch (IllegalArgumentException e) {
            /*
             * In some rare case, dateString is in the format of `2017-03-03T09:48:04Z` and
             * with that foramt, the `dateTime` formatter will throw `IllegalArgumentException`.
             * Therefore, we try to parse the date with `dateTimeNoMillis` formatter
             */
            return DateSerializer.formatterWithoutMS.parseDateTime(dateString).toDate();
        }
    }

    /**
     * Serialize a Date object to string
     * @param date the date object
     * @return the string representation of a date
     */
    static String stringFromDate(Date date) {
        return DateSerializer.formatterWithMS.print(new DateTime(date));
    }
}
