package com.gustavok.peach.notification;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDService extends InstanceIDListenerService {
    private static final String TAG = "GCMMyInstIDServ";

    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "onTokenRefresh...");

        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
