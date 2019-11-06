package com.example.callyourmother.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.callyourmother.R

/**
 * SplashScreen - entry point for the application which starts with the splash screen
 */
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onResume() {
        super.onResume()
        val transitionTime = 2500L
        Handler().postDelayed( {
            startActivity(Intent(this, MainActivity::class.java))
        }, transitionTime)
    }
}
