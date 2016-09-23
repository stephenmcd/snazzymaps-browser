package com.example.snazzymaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Starting point for the app. Provides the search interface for Snazzy Maps,
 * and displays results as a grid of styled static maps.
 */
public class MainActivity extends AppCompatActivity implements
        ApiTask.OnAsyncTaskPostExecuteHandler,
        GridAdapter.OnGridItemClickHandler,
        SearchView.OnQueryTextListener {

    private static final String STYLES_JSON = "styles_json";

    /**
     * The JSON returned from the Snazzy Maps API. We keep a reference to it so that
     * we can restore results if the screen orientation changes.
     */
    private String mStylesJson;

    private ProgressDialog mProgress;

    private SearchView mSearchView;

    private GridView mGridView;

    /**
     * Sets up the progress dialog, and either restores previously retrieved
     * styles (eg if the screen orientation changes), or loads some styles if
     * the app is first starting.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.grid);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.loading));

        if (savedInstanceState != null) {
            mStylesJson = savedInstanceState.getString(STYLES_JSON);
            populateGrid();
        } else {
            onQueryTextSubmit("");
        }
    }

    /**
     * Restores previously retrieved styles.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STYLES_JSON, mStylesJson);
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets up the search interface.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grid_menu, menu);
        mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    /**
     * Starts the process of retrieving results from the Snazzy Maps API, and also
     * first validates the both API keys have been configured.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        String snazzyMapsApiKey = getString(R.string.snazzy_maps_key);
        String googleMapsApiKey = getString(R.string.google_maps_key);
        if (snazzyMapsApiKey.length() == 0 || googleMapsApiKey.length() == 0) {
            Toast.makeText(this, getString(R.string.no_api_key), Toast.LENGTH_LONG).show();
        } else {
            new ApiTask(this, snazzyMapsApiKey).execute(query);
            mProgress.show();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        return true;
    }

    /**
     * Handler for {@link ApiTask.OnAsyncTaskPostExecuteHandler} that is called
     * when results are available from the Snazzy Maps API.
     *
     * @param stylesJson The result JSON.
     */
    @Override
    public void onAsyncTaskPostExecute(String stylesJson) {
        mProgress.hide();
        mSearchView.clearFocus(); // Ensures keyboard is removed.
        mStylesJson = stylesJson;
        populateGrid();
    }

    /**
     * Populates the GridView with styled static maps.
     */
    private void populateGrid() {
        GridAdapter adapter = new GridAdapter(this, mStylesJson);
        mGridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Handler for {@link GridAdapter.OnGridItemClickHandler} that is called
     * when one of the styled static maps are clicked on. We load the full map
     * activity, and pass it the JSON for the corresponding style.
     *
     * @param style The style instance for the map clicked on.
     */
    @Override
    public void onGridItemClick(SnazzyMapsStyle style) {
        Bundle bundle = new Bundle();
        bundle.putString(MapActivity.JSON_ID, style.toString());
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * Dynamically sets the number of GridView columns to support landscape and
     * portrait orientations.
     */
    private void setGridColumns() {
        int cols = (int) ((float) getWindowManager().getDefaultDisplay().getWidth() / (float) 500);
        mGridView.setNumColumns(cols);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setGridColumns();
    }

    @Override
    public void onResume() {
        super.onResume();
        setGridColumns();
    }

}
