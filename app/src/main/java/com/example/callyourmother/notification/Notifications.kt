package com.example.callyourmother.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import android.os.Build.VERSION
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import java.util.*


class Notifications {

    public fun notify(context: Context,title: String,description: String, CHANNEL_ID: String, iconID: Int,
               notificationID: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
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

    public fun createNotificationChannel(context: Context, CHANNEL_ID: String, CHANNEL_NAME: String, CHANNEL_DESCRIPTION: String) {
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

    public fun setScheduledNotification(context : Context, schedule: Int, lastCalled: Calendar,
                                        userID: String, title: String, body: String) {
        val manager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent : Intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("notificationBody", body)
        intent.putExtra("notificationTitle", title)
        intent.putExtra("userID", userID)

        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(context,0,intent,0)

        //Today's date
        val today : Calendar = Calendar.getInstance()
        today.timeInMillis = System.currentTimeMillis()

        //Scheduling the next call
        val nextCall : Calendar = lastCalled

        when(schedule) {
            1 -> nextCall.add(Calendar.DAY_OF_YEAR, 1)
            7 -> nextCall.add(Calendar.DAY_OF_YEAR, 7)
            14 -> nextCall.add(Calendar.DAY_OF_YEAR, 14)
            30 -> nextCall.add(Calendar.DAY_OF_YEAR, 30)
            365 -> nextCall.add(Calendar.DAY_OF_YEAR, 365)
        }

        //Determine the interval in which alarms should be made
        var interval : Long = 86400000
        when(schedule) {
            1 -> interval = interval
            7 -> interval = interval*7
            14 -> interval = interval*14
            30 -> interval = interval*30
            365 -> interval = interval*365
        }

        if(nextCall.before(today)) {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, today.timeInMillis, interval, pendingIntent)
        }else {
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextCall.timeInMillis, interval, pendingIntent)
        }
    }

    //For the video
    public fun setTimedNotification(context: Context) {
        val manager : AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent : Intent = Intent(context, AlarmReceiver::class.java)
        val bundle = Bundle()

        bundle.putString("notificationBody", "You should call your mother!")
        bundle.putString("notificationTitle", "CAll YOUR MOTHER")
        bundle.putString("userID", "203-410-9815")

        intent.putExtra("Bundle", bundle)

//        intent.putExtra("notificationBody", "You should call your mother!")
//        intent.putExtra("notificationTitle", "CAll YOUR MOTHER")
//        intent.putExtra("userID", "203-410-9815")

        val pendingIntent : PendingIntent = PendingIntent.getBroadcast(context,0,intent,0)

        val nextCall : Calendar = Calendar.getInstance()
        nextCall.timeInMillis = System.currentTimeMillis()

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextCall.timeInMillis, 10, pendingIntent)
    }

    companion object {
        fun phoneNumbertoID(userID: String) : String {
            return userID.trim().replace("(", "").replace(" ", "")
                    .replace(")", "").replace("-","")
        }
    }

}