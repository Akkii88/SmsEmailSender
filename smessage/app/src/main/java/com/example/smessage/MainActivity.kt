package com.example.smessage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If already logged in, go directly to HomeActivity
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("email", currentUser.email)
                putExtra("phone", "8684849586") // ✅ manually added phone number
            }
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        oneTapClient = Identity.getSignInClient(this)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()

        val googleSignInButton = findViewById<SignInButton>(R.id.googleSignInButton)

        googleSignInButton.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, 1001,
                        null, 0, 0, 0
                    )
                }
                .addOnFailureListener(this) { e ->
                    Toast.makeText(this, "No Google accounts found: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1001) {
            try {
                val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(data)
                val selectedEmail = credential.id

                Toast.makeText(this, "Selected: $selectedEmail", Toast.LENGTH_LONG).show()
                Log.d("AccountSelection", "User selected: $selectedEmail")

                // Redirect to HomeActivity with email and manually added phone
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("email", selectedEmail)
                    putExtra("phone", "8684849586") // ✅ manually added phone number
                }
                startActivity(intent)
                finish()

            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to get account: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
