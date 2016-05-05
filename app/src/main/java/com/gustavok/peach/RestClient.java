package com.gustavok.peach;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public final class RestClient {
    private static final String BASE_URL = "http://ojur.com.br:2002/";
    private static final String TAG = "RestClient";

    private static final SyncHttpClient CLIENT = new SyncHttpClient();

    private static String getAbsoluteUrl(String method) {
        return BASE_URL + method;
    }

    public static RestResponse getAllSenators() {
        return getSenatorsList(null);
    }

    public static RestResponse getAllVotes() {
        RequestParams params = new RequestParams("info", "");
        return getSenatorsList(params);
    }

    private static RestResponse getSenatorsList(RequestParams params) {
        final RestResponse restResponse = new RestResponse();

        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received %s as response", response));
                restResponse.setResponse(response);
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

        String method = "senadores";
        CLIENT.get(getAbsoluteUrl(method), params, jsonHttpResponseHandler);
        return restResponse;
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
