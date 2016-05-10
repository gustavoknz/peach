package com.gustavok.peach;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public final class RestClient {
    private static final int PORT = 2002;
    private static final String BASE_URL = "http://ojur.com.br";
    private static final String TAG = "RestClient";

    private static final AsyncHttpClient CLIENT = new AsyncHttpClient(PORT);

    private static String getAbsoluteUrl(String method) {
        return BASE_URL + method;
    }

    public static void getAllVotes(SenatorsCallbackInterface callback) {
        RequestParams params = new RequestParams("info", "");
        getSenatorsList(params, callback);
    }

    private static void getSenatorsList(RequestParams params, final SenatorsCallbackInterface callback) {
        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received a response: %s", response));
                try {
                    JSONArray jsonArray = response.getJSONArray("senadores");
                    Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
                    callback.onSuccess(senatorsArray);
                    Log.d(TAG, String.format(Locale.getDefault(), "Got %d senators as response", senatorsArray.length));
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("Failure 1. statusCode: %d; errorResponse: %s", statusCode, responseString), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Failure 2. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }
        };
        //jsonHttpResponseHandler.setUseSynchronousMode(true);

        String method = "/senadores";
        CLIENT.get(getAbsoluteUrl(method), params, jsonHttpResponseHandler);
    }
}
