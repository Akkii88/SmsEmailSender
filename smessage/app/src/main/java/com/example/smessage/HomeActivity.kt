package com.example.smessage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Get the current user from Firebase Auth
        val user = FirebaseAuth.getInstance().currentUser

        // Check if the user is signed in
        if (user != null) {
            // Retrieve email and phone from Firebase
            val email = user.email ?: "Not available"
            val phone = user.phoneNumber ?: "Not available"

            // Set the values to the TextViews
            findViewById<TextView>(R.id.textViewPhone).text = "Phone: $phone"
            findViewById<TextView>(R.id.textViewEmail).text = "Email: $email"
        }

        // Send Message button click listener
        findViewById<Button>(R.id.buttonSendMessage).setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        // Send Email button click listener
        findViewById<Button>(R.id.buttonSendEmail).setOnClickListener {
            startActivity(Intent(this, EmailActivity::class.java))
        }

        // Logout button click listener
        findViewById<Button>(R.id.buttonLogout).setOnClickListener {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Navigate back to phone verification page
            val intent = Intent(this, PhoneAuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
