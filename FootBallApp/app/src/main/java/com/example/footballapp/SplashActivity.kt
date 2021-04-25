package com.example.footballapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

class SplashActivity : AppCompatActivity() {

    lateinit var splashRunnable: Runnable
    private val TAG = "Splash1"

    private var skip = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        splashRunnable = Runnable {
            if(skip) {
                Log.d(TAG, "inside runnable skip1: $skip")
                val intent = Intent(this@SplashActivity,LogInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        Handler(Looper.getMainLooper()).postDelayed( splashRunnable, 12000)
    }

    fun nextScreen(view: View){
        //Handler(Looper.getMainLooper()).removeCallbacks(splashRunnable)
        skip = false
        Log.d(TAG, "inside skip1 button: $skip")
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
        val intent = Intent(this@SplashActivity,LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

}