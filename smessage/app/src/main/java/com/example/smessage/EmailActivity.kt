package com.example.smessage

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EmailActivity : AppCompatActivity() {

    private lateinit var recipientEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_activity)

        recipientEditText = findViewById(R.id.editTextRecipient)
        messageEditText = findViewById(R.id.editTextMessage)
        sendButton = findViewById(R.id.buttonSendEmail)

        sendButton.setOnClickListener {
            val recipient = recipientEditText.text.toString()
            val message = messageEditText.text.toString()

            if (recipient.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Send Email?")
                .setMessage("Do you want to send this email to $recipient?")
                .setPositiveButton("Yes") { _, _ ->
                    saveEmailToFirestore(recipient, message)
                    Toast.makeText(this, "Email sent to $recipient", Toast.LENGTH_LONG).show()
                    recipientEditText.text.clear()
                    messageEditText.text.clear()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun saveEmailToFirestore(recipient: String, message: String) {
        val emailData = hashMapOf(
            "fromEmail" to (currentUser?.email ?: "Unknown"),
            "to" to recipient,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("emails").add(emailData)
    }
}
