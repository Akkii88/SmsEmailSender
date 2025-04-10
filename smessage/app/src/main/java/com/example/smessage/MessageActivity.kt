package com.example.smessage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageActivity : AppCompatActivity() {

    private lateinit var recipientEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        recipientEditText = findViewById(R.id.editTextMessageRecipient)
        messageEditText = findViewById(R.id.editTextSmsMessage)
        sendButton = findViewById(R.id.buttonSendSms)

        sendButton.setOnClickListener {
            val recipient = recipientEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            if (recipient.isNotEmpty() && message.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("Confirm Send")
                    .setMessage("Are you sure you want to send this message?")
                    .setPositiveButton("Yes") { _, _ ->
                        saveMessageToFirestore(recipient, message)
                        Toast.makeText(this, "Message sent to $recipient", Toast.LENGTH_SHORT).show()
                        recipientEditText.text.clear()
                        messageEditText.text.clear()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Please enter both recipient and message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveMessageToFirestore(recipient: String, message: String) {
        val messageData = hashMapOf(
            "fromPhone" to (currentUser?.phoneNumber ?: "Unknown"),
            "to" to recipient,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("messages").add(messageData)
    }
}
