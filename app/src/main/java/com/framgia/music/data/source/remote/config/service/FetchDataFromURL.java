package com.framgia.music.data.source.remote.config.service;

import android.os.AsyncTask;
import android.util.Log;
import com.framgia.music.screen.OnFetchDataListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;

/**
 * Created by Admin on 3/9/2018.
 */

public class FetchDataFromURL extends AsyncTask<String, Void, String> {
    
    private static final String TAG = "FetchDataFromURL";
    private OnFetchDataListener mOnFetchDataListener;

    public FetchDataFromURL(OnFetchDataListener fetchDataListener) {
        mOnFetchDataListener = fetchDataListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data = "";
        try {
            data = getJSONStringFromURL(strings[0]);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
        } catch (JSONException e) {
            Log.e(TAG, "doInBackground: ", e);
        }
        return data;
    }

    @Override
    protected void onPostExecute(String collection) {
        super.onPostExecute(collection);
        mOnFetchDataListener.onFetchDataSuccess(collection);
    }

    private String getJSONStringFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setDoOutput(true);
        urlConnection.connect();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), Charset.forName("UTF-8")));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String jsonString = builder.toString();
        reader.close();
        urlConnection.disconnect();
        return jsonString;
    }
}
