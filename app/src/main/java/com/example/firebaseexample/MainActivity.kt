package com.example.firebaseexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    // Initialize database reference
    private val databaseReference = FirebaseDatabase.getInstance().reference.child("items")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is logged in
        if (isLoggedIn()) {
            // If logged in, show home fragment
            showHomeFragment()

        } else {
            // If not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginFirebase::class.java))
            finish()
        }
    }

    // Function to check if user is logged in
    private fun isLoggedIn(): Boolean {
        // Check if the current user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    // Function to show the home fragment
    private fun showHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
}
