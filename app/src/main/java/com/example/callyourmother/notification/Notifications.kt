package com.example.callyourmother.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.callyourmother.fragments.UserFragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import android.os.Build.VERSION
import androidx.core.app.NotificationManagerCompat


class Notifications {

    fun notify(context: Context,title: String,description: String, CHANNEL_ID: String, iconID: Int,
               notificationID: Int) {
        val intent = Intent(context, UserFragment::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent : PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconID)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationID, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context, CHANNEL_ID: String, CHANNEL_NAME: String, CHANNEL_DESCRIPTION: String) {
        if(VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val NotificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            NotificationManager.createNotificationChannel(channel)
        }
    }
}