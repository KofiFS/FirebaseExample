package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for the splash screen activity
        setContentView(R.layout.activity_splash)

        // Handler to delay opening the MainActivity
        Handler().postDelayed({
            // Start MainActivity after 2 seconds
            startActivity(Intent(this, MainActivity::class.java))
            // Finish the current activity to prevent the user from going back to the splash screen
            finish()
        }, 2500) // 2500 milliseconds = 2.5 seconds
    }
}
