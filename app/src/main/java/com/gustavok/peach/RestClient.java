package com.gustavok.peach;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RestClient {
    private static final String BASE_URL = "http://legis.senado.leg.br/dadosabertos/";
    private static final String TAG = "RestClient";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public List<Senator> getAllSenators() {
        final List<Senator> senators = new ArrayList<>();

        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received %s as response", response));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, String.format("Found %d senators", response.length()));
                Senator[] senatorsArray = new Gson().fromJson(response.toString(), Senator[].class);
                senators.addAll(Arrays.asList(senatorsArray));
                Log.d(TAG, String.format("Added %d senators to my list", senators.size()));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, String.format("Received %s as responseString", responseString));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Failure 1. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("Failure 2. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("Failure 3. statusCode: %d; responseString: %s", statusCode, responseString), throwable);
            }
        };

        //String url = "senador/lista/atual";
        String url = "1.1.1.1:5050/getSenators";
        client.get(getAbsoluteUrl(url), jsonHttpResponseHandler);
        return senators;
    }

    public List<SenatorVote> getAllVotes() {
        final List<SenatorVote> senatorsVotes = new ArrayList<>();

        // SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
        JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, String.format("Received %s as response", response));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, String.format("Found %d senators votes", response.length()));
                SenatorVote[] senatorsArray = new Gson().fromJson(response.toString(), SenatorVote[].class);
                senatorsVotes.addAll(Arrays.asList(senatorsArray));
                Log.d(TAG, String.format("Added %d senators votes to my list", senatorsVotes.size()));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, String.format("Received %s as responseString", responseString));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, String.format("Failure 1. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, String.format("Failure 2. statusCode: %d; errorResponse: %s", statusCode, errorResponse), throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, String.format("Failure 3. statusCode: %d; responseString: %s", statusCode, responseString), throwable);
            }
        };

        //String url = "senador/lista/atual";
        String url = "1.1.1.1:5050/getSenators";
        client.get(getAbsoluteUrl(url), jsonHttpResponseHandler);
        return senatorsVotes;
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
