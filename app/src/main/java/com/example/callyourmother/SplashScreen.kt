package com.example.callyourmother

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log

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
