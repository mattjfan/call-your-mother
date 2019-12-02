package com.example.callyourmother.fragments


import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_timer_debug.*
import com.example.callyourmother.R
import com.example.callyourmother.notification.Notifications



class TimerDebug : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        to_home_fragment.setOnClickListener {
            view.findNavController().navigate(TimerDebugDirections.actionTimerDebugToHomeFragment("Test", "Test", "Test", false))
        }

        test.setOnClickListener {
            Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show()
            val notificationManager: Notifications = Notifications()
            notificationManager.createNotificationChannel(this.requireContext(), "0", "main", "")
            notificationManager.setTimedNotification(this.requireContext())
            //notificationManager.notify(this.requireContext(), "Call your mother", "call", "0",
              //      R.drawable.logo_blob, 1)
        }
    }
}
