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
