package com.example.snazzymaps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that takes the JSON for a list of styles from the Snazzy Maps API, and
 * populates a GridView with a styled static map for each result. Also provides the
 * OnGridItemClickHandler interface, for delegating the map click back to the calling
 * Activity.
 */
class GridAdapter extends BaseAdapter {

    private final static String TAG = GridAdapter.class.getSimpleName();

    /**
     * The list of {@link SnazzyMapsStyle} objects created from the supplied JSON.
     */
    private List<SnazzyMapsStyle> mStyles = new ArrayList<>();

    /**
     * The calling Activity.
     */
    private Context mContext;

    /**
     * Delegates click handling to the calling Activity.
     */
    interface OnGridItemClickHandler {
        void onGridItemClick(SnazzyMapsStyle style);
    }

    /**
     * Converts the JSON for the list of style results from the Snazzy Maps API into
     * {@link SnazzyMapsStyle} objects.
     *
     * @param context The calling Activity.
     * @param stylesJson The JSON for the list of style results from the Snazzy Maps API.
     */
    GridAdapter(Context context, String stylesJson) {
        mContext = context;
        try {
            JSONArray styles = new JSONObject(stylesJson).getJSONArray("styles");
            for (int i = 0; i < styles.length(); i++) {
                mStyles.add(new SnazzyMapsStyle(styles.getString(i)));
            }
            if (mStyles.size() == 0) {
                Toast.makeText(context, context.getString(R.string.no_styles),
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Creates a style static map for a single item in the GridView.
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = ((LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.mapView = (MapView) convertView.findViewById(R.id.grid_item);
            convertView.setTag(holder);
            holder.initializeMapView();
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SnazzyMapsStyle style = (SnazzyMapsStyle) getItem(position);
        holder.mapView.setTag(style);
        if (holder.map != null) {
            style.applyToMap(holder.map);
        }

        return convertView;
    }

    private class ViewHolder implements OnMapReadyCallback {

        MapView mapView;
        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(mContext.getApplicationContext());
            map = googleMap;
            final SnazzyMapsStyle style = (SnazzyMapsStyle) mapView.getTag();
            style.applyToMap(map);
            map.getUiSettings().setMapToolbarEnabled(false);  // Removes the map button.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);  // Shows the map.
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    ((OnGridItemClickHandler) mContext).onGridItemClick(style);
                }
            });
        }

        void initializeMapView() {
            if (mapView != null) {
                mapView.onCreate(null);
                mapView.getMapAsync(this);
            }
        }

    }

    @Override
    public int getCount() {
        return mStyles.size();
    }

    @Override
    public Object getItem(int position) {
        return mStyles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((SnazzyMapsStyle) getItem(position)).id;
    }

}
