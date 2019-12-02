package com.example.callyourmother.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri

class ActionReceiver() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val number = intent.getStringExtra("userID")
        val callIntent : Intent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse(number)
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(callIntent)
    }
}
