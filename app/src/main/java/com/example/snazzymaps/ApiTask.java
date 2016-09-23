package com.example.snazzymaps;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AsyncTask that retrieves results from the Snazzy Maps API, and delegates handling
 * results to the calling Activity.
 */
class ApiTask extends AsyncTask<String, Void, String> {

    private final static String TAG = ApiTask.class.getSimpleName();

    /**
     * The calling Activity.
     */
    private OnAsyncTaskPostExecuteHandler mPostExecuteHandler;

    private String mApiKey;

    /**
     * Delegates results handling to the calling Activity.
     */
    interface OnAsyncTaskPostExecuteHandler {
        void onAsyncTaskPostExecute(String stylesJson);
    }

    ApiTask(OnAsyncTaskPostExecuteHandler postExecuteHandler, String apiKey) {
        mPostExecuteHandler = postExecuteHandler;
        mApiKey = apiKey;
    }

    /**
     * Makes the HTTP request to the Snazzy Maps API.
     *
     * @param params The search query.
     * @return JSON for the resulting styles.
     */
    protected String doInBackground(String... params) {
        String url = String.format("https://snazzymaps.com/explore.json?text=%s&key=%s",
                params[0], mApiKey);
        Request request = new Request.Builder().url(url).build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    protected void onPostExecute(String stylesJson) {
        mPostExecuteHandler.onAsyncTaskPostExecute(stylesJson);
    }
}
