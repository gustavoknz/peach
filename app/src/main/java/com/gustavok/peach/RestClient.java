package com.gustavok.peach;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public final class RestClient {
    private static final String BASE_URL = "http://ojur.com.br:2002/";
    private static final String TAG = "RestClient";

    private static final AsyncHttpClient CLIENT = new AsyncHttpClient();

    private static String getAbsoluteUrl(String method) {
        return BASE_URL + method;
    }

    public static List<Senator> getAllSenators(Context context) {
        List<Senator> senators = getSenatorsList(null);
        for (Senator senator: senators) {
            String imageName = String.format(Locale.getDefault(), "%d.jpg", senator.getId());
            SenatorsManager.getInstance().saveToInternalStorage(imageName, senator.getImagem(), context);
        }
        return senators;
    }

    public static List<Senator> getAllVotes() {
        RequestParams params = new RequestParams("info", "");
        return getSenatorsList(params);
    }

    private static List<Senator> getSenatorsList(RequestParams params) {
        final List<Senator> senators = new ArrayList<>();

        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received %s as response", response));
                try {
                    JSONArray jsonArray = response.getJSONArray("senadores");
                    Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
                    senators.addAll(Arrays.asList(senatorsArray));
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON. Response: " + response, e);
                }
                Log.d(TAG, String.format("Added %d senators to my list", senators.size()));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Failure 1. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }
        };

        String method = "senadores";
        CLIENT.get(getAbsoluteUrl(method), params, jsonHttpResponseHandler);
        return senators;
    }

    /*private class Tuple {
        public final Integer color;
        public final String text;

        public Tuple(int _color, String _text) {
            this.color = _color;
            this.text = _text;
        }
    }

    private class SAXTreeStructure extends DefaultHandler {
        public final List<Tuple> responseViews = new ArrayList<Tuple>();

        public void startElement(String namespaceURI, String localName, String rawName, Attributes atts) {
            responseViews.add(new Tuple(6575, "Start Element: " + rawName));
        }

        public void endElement(String namespaceURI, String localName, String rawName) {
            responseViews.add(new Tuple(345, "End Element: " + rawName));
        }

        public void characters(char[] data, int off, int length) {
            if (length > 0 && data[0] != '\n') {
                responseViews.add(new Tuple(453, "Characters:  " + new String(data, off, length)));
            }
        }
    }*/
}
