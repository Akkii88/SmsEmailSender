package com.example.smessage

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var phoneEditText: EditText
    private lateinit var otpEditText: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var verifyOtpButton: Button
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        auth = FirebaseAuth.getInstance()

        phoneEditText = findViewById(R.id.editTextPhone)
        otpEditText = findViewById(R.id.editTextOTP)
        sendOtpButton = findViewById(R.id.buttonSendOTP)
        verifyOtpButton = findViewById(R.id.buttonVerifyOTP)

        sendOtpButton.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()
            if (!phone.startsWith("+")) phoneEditText.setText("+91$phone") // Assuming India, change accordingly
            if (phone.isEmpty() || !phone.matches(Regex("^\\+[1-9]\\d{9,14}$"))) {
                Toast.makeText(this, "Enter valid phone number with country code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendVerificationCode(phone)
        }

        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString().trim()
            if (otp.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationCode(phone: String) {
        // Create a callback for phone verification
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@PhoneAuthActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                this@PhoneAuthActivity.verificationId = verificationId
                Toast.makeText(this@PhoneAuthActivity, "OTP sent", Toast.LENGTH_SHORT).show()
            }
        }

        // Send the verification code
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone, // The phone number to verify
            60L, // Timeout duration (seconds)
            TimeUnit.SECONDS, // TimeUnit
            this, // Activity context
            callbacks // The callback to handle verification
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // After phone number verification, navigate to Google login
                    startActivity(Intent(this, LoginActivity::class.java)) // or MainActivity
                    finish()
                } else {
                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
