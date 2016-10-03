package io.skygear.skygear;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Skygear Location Serializer.
 */
public class LocationSerializer {
    /**
     * Serialize a location object.
     *
     * @param location the location
     * @return the json object
     */
    public static JSONObject serialize(Location location) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("$type", "geo");
            jsonObject.put("$lat", location.getLatitude());
            jsonObject.put("$lng", location.getLongitude());

            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Deserialize a location object from JSON object.
     *
     * @param jsonObject the JSON object
     * @return the location
     * @throws JSONException the JSON exception
     */
    public static Location deserialize(JSONObject jsonObject) throws JSONException {
        String typeValue = jsonObject.getString("$type");
        if (typeValue.equals("geo")) {
            double lat = jsonObject.getDouble("$lat");
            double lng = jsonObject.getDouble("$lng");

            Location location = new Location("skygear");
            location.setLatitude(lat);
            location.setLongitude(lng);

            return location;
        }

        throw new JSONException("Invalid $type value: " + typeValue);
    }

    /**
     * Determines whether an object is a JSON object in Skygear defined location format.
     *
     * @param object the object
     * @return the indicating boolean
     */
    public static boolean isLocationFormat(Object object) {
        try {
            JSONObject jsonObject = (JSONObject) object;
            String type = jsonObject.getString("$type");
            double lat = jsonObject.getDouble("$lat");
            double lng = jsonObject.getDouble("$lng");

            return type.equals("geo") &&
                    lat > -90 && lat < 90 &&
                    lng > -180 && lng < 180;
        } catch (ClassCastException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
    }
}
