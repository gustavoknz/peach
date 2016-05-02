package com.gustavok.peach;

import android.app.Application;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    @Test
    public void testParse() throws Exception {
        JSONObject json = new JSONObject("{\"senadores\":[{\"id\":1,\"nome\":\"Acir\",\"estado\":\"TO\",\"partido\":\"PDT\",\"voto\":\"\u0001\"}]}");
        JSONArray jsonArray = json.getJSONArray("senadores");
        Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
        Assert.assertEquals("Expected different number of senators", 1, senatorsArray.length);
        Assert.assertEquals("Expected different id.", 1, senatorsArray[0].getId());
        Assert.assertEquals("Expected different name.", "Acir", senatorsArray[0].getNome());
        Assert.assertEquals("Expected different state.", "TO", senatorsArray[0].getEstado());
        Assert.assertEquals("Expected different party.", "PDT", senatorsArray[0].getPartido());
        Assert.assertEquals("Expected different vote.", "", senatorsArray[0].getVoto());
    }
}
