package com.example.indigitalstudy.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.indigitalstudy.databinding.ActivitySendOtpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class SendOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendOtpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val inputMobile: EditText = binding.inputMobile
        val buttonGetOtp: Button = binding.btnGetOTP

        val processBar: ProgressBar = binding.progressBar

        val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(@NonNull phoneAuthCredential: PhoneAuthCredential) {
                processBar.visibility = View.GONE
                buttonGetOtp.visibility = View.VISIBLE
                val code: String? = phoneAuthCredential.smsCode
            }

            override fun onVerificationFailed(@NonNull firebaseException: FirebaseException) {
                processBar.visibility = View.GONE
                buttonGetOtp.visibility = View.VISIBLE
                showToast("${firebaseException.message}")
            }

            override fun onCodeSent(
                @NonNull verificationId: String,
                @NonNull forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, forceResendingToken)
                processBar.visibility = View.GONE
                buttonGetOtp.visibility = View.VISIBLE

                val email = intent.getStringExtra("email")
                val password = intent.getStringExtra("password")
                val image = intent.getStringExtra("image")
                val name = intent.getStringExtra("name")

                val intent = Intent(applicationContext, VerifyOTPActivity::class.java)
                intent.putExtra("mobile", inputMobile.text.toString())
                intent.putExtra("verificationId", verificationId)
                if (image != null) {
                    intent.putExtra("image", image.toString())
                }
                intent.putExtra("email", email.toString())
                intent.putExtra("name", name.toString())
                intent.putExtra("password", password.toString())

                startActivity(intent)
            }
        }

        buttonGetOtp.setOnClickListener {
            if (inputMobile.text.toString().trim().isEmpty()) {
                showToast("Enter mobile")
                return@setOnClickListener
            } else {
                processBar.visibility = View.VISIBLE
                buttonGetOtp.visibility = View.INVISIBLE

                val phoneNumber = binding.inputMobile.text.toString()


                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+7$phoneNumber")       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}