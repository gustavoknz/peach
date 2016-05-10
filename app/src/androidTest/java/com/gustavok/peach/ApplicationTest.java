package com.gustavok.peach;

import android.app.Application;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final String TAG = "ApplicationTest";

    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testParse() throws Exception {
        JSONObject json = new JSONObject("{\"senadores\":[{\"id\":1,\"nome\":\"Acir Gurgacz\",\"estado\":\"TO\",\"partido\":\"PDT\",\"voto\":\"1\",\"url\":\"http://www.senado.leg.br/senadores/img/fotos-oficiais/senador4981.jpg\"}]}");
        JSONArray jsonArray = json.getJSONArray("senadores");
        Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
        Senator s = senatorsArray[0];
        Assert.assertEquals("Expected different number of senators", 1, senatorsArray.length);
        Assert.assertEquals("Expected different id.", 1, s.getId());
        Assert.assertEquals("Expected different name.", "Acir Gurgacz", s.getNome());
        Assert.assertEquals("Expected different state.", "TO", s.getEstado());
        Assert.assertEquals("Expected different party.", "PDT", s.getPartido());
        Assert.assertEquals("Expected different vote.", 1, s.getVoto());
        Assert.assertEquals("Expected different url.", "http://www.senado.leg.br/senadores/img/fotos-oficiais/senador4981.jpg", s.getUrl());
    }

    //@Test
    public void testFinishVoting() throws Exception {
        AsyncHttpClient client = new AsyncHttpClient(2002);
        String restApiUrl = "http://ojur.com.br/add_vote";

        JSONObject jsonParams;
        Random random = new Random();
        StringEntity entity;
        for (int i = 50; i <= 81; i++) {
            jsonParams = new JSONObject();
            jsonParams.put("id", i);
            jsonParams.put("voto", random.nextInt(4));
            entity = new StringEntity(jsonParams.toString());
            AsyncHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                    JSONObject res;
                    try {
                        res = new JSONObject(responseBody);
                        Log.d(TAG, "Response from server: " + res.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            };
            client.post(getContext(), restApiUrl, entity, "application/json", responseHandler);
        }
    }
}
