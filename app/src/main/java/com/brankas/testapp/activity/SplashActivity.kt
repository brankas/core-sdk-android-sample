package com.brankas.testapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.brankas.testapp.R

/**
 * Author: Ejay Torres
 * Email: ejay.torres@brank.as
 */

class SplashActivity : AppCompatActivity() {
    val delay = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        /**
         * Calls MainActivity after 2 seconds
         */
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, delay)
    }
}