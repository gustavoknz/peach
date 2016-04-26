package com.gustavok.peach;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RestClient {
    private static final String BASE_URL = "http://legis.senado.leg.br/dadosabertos/";
    private static final String TAG = "RestClient";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public void getAllSenators() {
        SaxAsyncHttpResponseHandler saxAsyncHttpResponseHandler = new SaxAsyncHttpResponseHandler<SAXTreeStructure>(new SAXTreeStructure()) {
            @Override
            public void onStart() {
                Log.d(TAG, "Starting getting senators");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, SAXTreeStructure saxTreeStructure) {
                Log.d(TAG, "statusCode: " + statusCode);
                debugHeaders(headers);
                debugHandler(saxTreeStructure);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, SAXTreeStructure saxTreeStructure) {
                Log.d(TAG, "statusCode: " + statusCode);
                debugHeaders(headers);
                debugHandler(saxTreeStructure);
            }

            private void debugHeaders(Header[] headers) {
                Log.d(TAG, "Headers: " + headers);
            }

            private void debugHandler(SAXTreeStructure handler) {
                for (Tuple t : handler.responseViews) {
                    Log.d(TAG, String.format("%d - %s", t.color, t.text));
                }
            }
        };

        String url = "senador/lista/atual";
        client.get(getAbsoluteUrl(url), null, saxAsyncHttpResponseHandler);
    }

    private class Tuple {
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
    }
}
