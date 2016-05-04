package com.gustavok.peach;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class SenatorsManager {
    private static final String TAG = "SenatorsManager";
    private static final SenatorsManager INSTANCE = new SenatorsManager();
    private static Context context;

    private static List<Senator> senators = new ArrayList<>();

    public static SenatorsManager getInstance() {
        return INSTANCE;
    }

    private SenatorsManager() {
        loadFromStorage();
        if (senators.size() <= 0) {
            loadFromUrl();
        }
    }

    private void loadFromUrl() {
        senators = RestClient.getAllSenators(context);
    }

    private List<Senator> loadFromStorage() {

        return new ArrayList<>();
    }

    public void saveToInternalStorage(String imageName, Bitmap bitmapImage, Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File path = new File(directory, imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing output", e);
                }
            }
        }
    }

    public Bitmap loadImageFromStorage(String path) {
        try {
            File f = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public void setContext(Context context) {
        SenatorsManager.context = context;
    }

    public List<Senator> getVotes() {
        return senators;
    }
}
