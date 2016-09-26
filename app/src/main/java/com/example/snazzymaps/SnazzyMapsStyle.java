/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.snazzymaps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Simple object representing a style retrieved from the Snazzy Maps API.
 */
class SnazzyMapsStyle {

    private final static String TAG = SnazzyMapsStyle.class.getSimpleName();

    /**
     * The original style JSON given.
     */
    private JSONObject mJson;

    int id;
    String name;
    String description;
    String author;
    String url;

    /**
     * Populates members with keys from the style JSON.
     *
     * @param json The style's JSON from the Snazzy Maps API.
     */
    SnazzyMapsStyle(String json) {
        try {
            mJson = new JSONObject(json);
            id = mJson.getInt("id");
            name = mJson.getString("name");
            description = mJson.getString("description");
            author = mJson.getJSONObject("createdBy").getString("name");
            url = mJson.getString("url");
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Shortcut for applying this style to a {@link GoogleMap}.
     *
     * @param map The {@link GoogleMap} object to style.
     */
    void applyToMap(GoogleMap map) {
        try {
            map.setMapStyle(new MapStyleOptions(mJson.getString("json")));
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public String toString() {
        return mJson.toString();
    }

}
