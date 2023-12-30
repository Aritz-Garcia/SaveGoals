package com.savegoals.savegoals;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.savegoals.savegoals.controlador.menu.ConfiguracionFragment;

public class AlarmNotification extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        createSimpleNotification(context);
    }

    private void createSimpleNotification(Context context) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ConfiguracionFragment.MY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Mete dinero")
                .setContentText("Es hora de meter dinero en tus objetivos")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
        Log.d("TAG", "createSimpleNotification");
    }
}
