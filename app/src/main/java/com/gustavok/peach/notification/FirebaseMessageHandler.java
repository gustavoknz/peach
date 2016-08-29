package com.gustavok.peach.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gustavok.peach.MainActivity;
import com.gustavok.peach.R;
import com.gustavok.peach.SenatorsManager;

public class FirebaseMessageHandler {
    private static int msgId = 0;
    private static final String TAG = "MyFirebaseMsgService";

    public static void handleNotification(Context context, String body) {
        Log.d(TAG, "Notifying message: " + body);
        sendNotification(body, context);
    }

    public static void handleVote(int id, int vote) {
        Log.d(TAG, String.format("Received vote id: %d; vote: %d", id, vote));
        SenatorsManager.getInstance().updateVote(id, vote);
        SenatorsManager.getInstance().updateVotesFromFirebase();
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private static void sendNotification(String messageBody, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++msgId, notificationBuilder.build());
    }
}
