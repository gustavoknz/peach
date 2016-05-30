package com.gustavok.peach.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class MyInstanceIDListenerService extends Service {

    private static final String TAG = "GCMInstIDListServ";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind...");
        return null;
    }
}
