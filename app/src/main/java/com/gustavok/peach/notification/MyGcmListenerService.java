package com.gustavok.peach.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.gustavok.peach.MainActivity;
import com.gustavok.peach.R;
import com.gustavok.peach.SenatorsManager;

import java.util.Locale;

public class MyGcmListenerService extends GcmListenerService {
    private static int msgId = 1;
    private static final String TAG = "GCMListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "From: " + from);
        int id = Integer.parseInt(data.getString("id"));
        Log.d(TAG, "id: " + id);
        int vote = Integer.parseInt(data.getString("vote"));
        Log.d(TAG, "vote: " + vote);
        boolean notify = Boolean.parseBoolean(data.getString("notify"));
        Log.d(TAG, "notify: " + notify);
        String msg = data.getString("msg");
        Log.d(TAG, "msg: " + msg);

        if (notify) {
            sendNotification(msg);
        } else {
            SenatorsManager.getInstance().addVote(id, vote);
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("Peach")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(msgId++, notificationBuilder.build());
    }
}
