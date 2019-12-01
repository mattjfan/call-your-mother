package com.example.callyourmother.notification

import android.content.BroadcastReceiver
import android.app.Notification;
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import androidx.core.app.NotificationCompat




class AlarmReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("notificationTitle")
        val body = intent.getStringExtra("notificationBody")
        val userID = intent.getIntExtra("userID", 0)

        val builder = NotificationCompat.Builder(context)

        val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                FLAG_ONE_SHOT)

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(userID, builder.build())
    }

}