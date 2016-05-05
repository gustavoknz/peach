package com.gustavok.peach;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class SenatorsManager {
    private static final String TAG = "SenatorsManager";
    private static final SenatorsManager INSTANCE = new SenatorsManager();
    private static final List<Senator> senators = new ArrayList<>();
    private static final String JSON_FILE_NAME = "senators.json";

    private SenatorsManager() {
        Log.d(TAG, "Trying to load from storage...");
        loadFromStorage();
        if ((senators == null) || senators.isEmpty()) {
            Log.d(TAG, "Trying to load from REST API...");
            loadFromUrl();
        }
    }

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    //region Load info from storage
    private void loadFromStorage() {
        try {
            loadJson();
            if ((senators == null) || senators.isEmpty()) {
                return;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found", e);
        } catch (IOException e) {
            Log.e(TAG, "Can not read file", e);
        } catch (JSONException e) {
            Log.e(TAG, "Could not parse json", e);
        }
        for (Senator senator : senators) {
            senator.setImagem(loadImageFromStorage(senator.getId()));
        }
    }

    private void loadJson() throws IOException, JSONException {
        File file = ContextHolder.getContext().getFileStreamPath(JSON_FILE_NAME);
        if (file == null || !file.exists()) {
            return;
        }
        InputStream inputStream = ContextHolder.getContext().openFileInput(JSON_FILE_NAME);

        if (inputStream == null) {
            Log.d(TAG, "Could not open json file");
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            String jsonString = stringBuilder.toString();

            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray("senadores");
            Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
            Collections.addAll(senators, senatorsArray);
        }
    }

    private Bitmap loadImageFromStorage(int imageId) {
        ContextWrapper cw = new ContextWrapper(ContextHolder.getContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        try {
            File f = new File(directory, String.format(Locale.getDefault(), "%d.jpg", imageId));
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not find file with imageId " + imageId, e);
        }
        return null;
    }
    //endregion

    //region Load info from server
    private void loadFromUrl() {
        JSONObject response = RestClient.getAllSenators().getResponse();
        if (response == null) {
            return;
        }
        serializeJson(response);
        try {
            JSONArray jsonArray = response.getJSONArray("senadores");
            Senator[] senatorsArray = new Gson().fromJson(jsonArray.toString(), Senator[].class);
            Collections.addAll(senators, senatorsArray);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON. Response: " + response, e);
        }
        Log.d(TAG, String.format("Added %d senators to my list", senators.size()));

        for (Senator senator : senators) {
            Bitmap bitmap = downloadBitmap(senator.getUrl());
            saveImagesToInternalStorage(senator.getId(), bitmap, ContextHolder.getContext());
        }
    }

    private void serializeJson(JSONObject senatorsJson) {
        String filename = JSON_FILE_NAME;
        try {
            FileOutputStream fos = ContextHolder.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(senatorsJson.toString().getBytes());
            fos.close();
            File filePath = new File(ContextHolder.getContext().getFilesDir(), filename);
            notifyFileAdded(filePath);
        } catch (Exception e) {
            Log.e(TAG, "Error writing json file", e);
        }
    }

    private Bitmap downloadBitmap(String senatorUrl) {
        RequestCreator rc = Picasso.with(ContextHolder.getContext()).load(senatorUrl);
        Bitmap image = null;
        try {
            image = rc.get();
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image from " + senatorUrl, e);
        }
        return image;
    }

    private void saveImagesToInternalStorage(int senatorId, Bitmap bitmapImage, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File path = new File(directory, String.format(Locale.getDefault(), "%d.jpg", senatorId));

        try {
            FileOutputStream fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            notifyFileAdded(path);
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
        }
    }

    private void notifyFileAdded(File path) {
        ContextHolder.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(path)));
    }
    //endregion

    public List<Senator> getVotes() {
        return senators;
    }
}
