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
    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime().withZoneUTC();

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
            jsonObject.put("$date", DateSerializer.formatter.print(new DateTime(date)));

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

            return DateSerializer.formatter.parseDateTime(dateString).toDate();
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
}
