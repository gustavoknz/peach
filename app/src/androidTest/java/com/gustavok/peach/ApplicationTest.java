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
        JSONObject json = new JSONObject("{\"senadores\":[{\"id\":1,\"nome\":\"Acir Gurgacz\",\"estado\":\"TO\",\"partido\":\"PDT\",\"voto\":\"1\",\"url\":\"http://www.senado.leg.br/senadores/img/fotos-oficiais/senador4981.jpg\"}]}");
        JSONArray jsonArray = json.getJSONArray("senadores");
        Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
        Senator s = senatorsArray[0];
        Assert.assertEquals("Expected different number of senators", 1, senatorsArray.length);
        Assert.assertEquals("Expected different id.", 1, s.getId());
        Assert.assertEquals("Expected different name.", "Acir Gurgacz", s.getNome());
        Assert.assertEquals("Expected different state.", "TO", s.getEstado());
        Assert.assertEquals("Expected different party.", "PDT", s.getPartido());
        Assert.assertEquals("Expected different vote.", "1", s.getVoto());
        Assert.assertEquals("Expected different url.", "http://www.senado.leg.br/senadores/img/fotos-oficiais/senador4981.jpg", s.getUrl());
    }
}
