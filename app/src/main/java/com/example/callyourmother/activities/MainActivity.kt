package com.example.callyourmother.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.callyourmother.R
import com.example.callyourmother.notification.Notifications

/**
 * MainActivity - this is the class which hosts the Fragment switches. Not much needs to be done here
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        // Rely on the up button presses
    }
}
