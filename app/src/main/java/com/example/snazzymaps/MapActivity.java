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

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Activity that displays a full styled map when one of the styled static maps
 * is clicked on. It also provides a dialog that shows information about the
 * style, such as its author and URL.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String JSON_ID = "json_id";

    /**
     * Initial location for the map.
     */
    static final LatLng START_LATLNG = new LatLng(40.7128, -74.0059);

    private SnazzyMapsStyle mStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Styles the map when its available, using the style JSON provided by the
     * previous activity.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mStyle = new SnazzyMapsStyle(bundle.getString(JSON_ID));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(START_LATLNG, 11));
            mStyle.applyToMap(googleMap);
            getSupportActionBar().setTitle(mStyle.name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Back button in action bar.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    /**
     * Click handler for the action bar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_dialog:  // Info button clicked, show the info dialog.
                showInfoDialog();
                return true;
            case android.R.id.home:  // Back button clicked, finish the activity.
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows a dialog that provides information about the style, such as its author and URL.
     */
    private void showInfoDialog() {
        String desc = mStyle.description.split(" ").length > 3 ? mStyle.description + "\n\n" : "";
        final SpannableString message = new SpannableString(getString(
                R.string.info_dialog_message, desc, mStyle.url));
        Linkify.addLinks(message, Linkify.ALL);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(getString(R.string.info_dialog_title, mStyle.name, mStyle.author))
                .setMessage(message)
                .create();
        dialog.show();

        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
