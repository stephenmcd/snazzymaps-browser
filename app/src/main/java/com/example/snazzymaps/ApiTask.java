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
     * The calling Activity that results are delegated to.
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
