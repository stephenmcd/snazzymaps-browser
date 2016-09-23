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
