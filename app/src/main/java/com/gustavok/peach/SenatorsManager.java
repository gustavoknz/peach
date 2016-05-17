package com.gustavok.peach;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SenatorsManager {
    private static final String TAG = "SenatorsManager";
    private static final SenatorsManager INSTANCE = new SenatorsManager();
    private static final String JSON_FILE_NAME = "senators.json";

    private final List<Senator> senators = new ArrayList<>();

    private SenatorsManager() {
        Log.d(TAG, "Loading from static JSON file");
        loadFromStaticJson();
    }

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    //region Load info from static json
    private void loadFromStaticJson() {
        String jsonString = getJSONString(ContextHolder.getContext());

        try {
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("senadores");
            Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
            Collections.addAll(senators, senatorsArray);
        } catch (JSONException e) {
            Log.e(TAG, "Error loading JSON", e);
        }
    }

    private String getJSONString(Context context) {
        String str = "";
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open(JSON_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(in);
            char[] inputBuffer = new char[100];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                str += String.copyValueOf(inputBuffer, 0, charRead);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading JSON", e);
        }

        return str;
    }
    //endregion

    public List<Senator> getVotes() {
        return senators;
    }

    public void setSenatorVotes(Senator[] senatorVotes) {
        for (Senator s1 : senators) {
            for (Senator s2 : senatorVotes) {
                if (s1.getId() == s2.getId()) {
                    s1.setVoto(s2.getVoto());
                    break;
                }
            }
        }
    }
}
