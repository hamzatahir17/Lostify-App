package com.example.lostify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String body = data.get("body");
            String type = data.get("type");

            if (!shouldShowNotification(type)) {
                return;
            }

            wakeUpScreen();
            handleNotification(title, body, type, data);
        }
    }

    private boolean shouldShowNotification(String type) {
        SharedPreferences prefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);

        if ("chat".equals(type)) {
            return prefs.getBoolean("notify_chat", true);
        }
        else if ("report_lost".equals(type) || "report_found".equals(type)) {
            return prefs.getBoolean("notify_found", true);
        }
        return true;
    }

    private void wakeUpScreen() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();

        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                    "Lostify:NotificationWakeLock"
            );
            wl.acquire(3000);
        }
    }

    private void handleNotification(String title, String message, String type, Map<String, String> data) {
        Intent intent;

        if ("chat".equals(type)) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("receiverId", data.get("senderId"));
        }
        else if ("report_lost".equals(type) || "report_found".equals(type)) {
            intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra("ITEM_NAME", data.get("itemName"));
            intent.putExtra("ITEM_LOCATION", data.get("location"));
            intent.putExtra("ITEM_DATE", data.get("date"));
            intent.putExtra("ITEM_TIME", data.get("time"));
            intent.putExtra("DESCRIPTION", data.get("description"));
            intent.putExtra("CATEGORY", data.get("category"));
            intent.putExtra("ITEM_IMAGE_URL", data.get("imageUrl"));
            intent.putExtra("USER_ID", data.get("userId"));
            intent.putExtra("ITEM_STATUS", "report_found".equals(type) ? "FOUND" : "LOST");
        }
        else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, new Random().nextInt(), intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = "lostify_alerts";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "App Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PUBLIC);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);
        }

        manager.notify(new Random().nextInt(), builder.build());
    }
}