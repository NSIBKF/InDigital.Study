package com.example.indigitalstudy.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.indigitalstudy.R
import com.example.indigitalstudy.databinding.ActivitySendOtpBinding
import com.example.indigitalstudy.databinding.ActivityVerifyOtpBinding
import com.example.indigitalstudy.utilities.Constants
import com.example.indigitalstudy.utilities.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding
    private lateinit var inputCode1: EditText
    private lateinit var inputCode2: EditText
    private lateinit var inputCode3: EditText
    private lateinit var inputCode4: EditText
    private lateinit var inputCode5: EditText
    private lateinit var inputCode6: EditText
    private lateinit var verificationId: String
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textMobile: TextView = binding.textMobile
        textMobile.text = String.format(
            "+7-%s", intent.getStringExtra("mobile")
        )

        assigningValueInputCode1UntilInputCode6()

        setupOTPInputs()

        val progressBar = binding.progressBar
        val buttonVerify: Button = binding.btnVerify
        verificationId = intent.getStringExtra("verificationId").toString()

        buttonVerify.setOnClickListener {
            if (inputCode1.text.trim().isEmpty() || inputCode2.text.trim().isEmpty() || inputCode3.text.trim().isEmpty()
                || inputCode4.text.trim().isEmpty() || inputCode5.text.trim().isEmpty() || inputCode6.text.trim().isEmpty()) {
                showToast("Please enter valid code")
                return@setOnClickListener
            }
            val code: String = inputCode1.text.toString() +
                    inputCode2.text.toString() + inputCode3.text.toString() +
                    inputCode4.text.toString() + inputCode5.text.toString() + inputCode6.text.toString()

            if (verificationId != null) {
                progressBar.visibility = View.VISIBLE
                buttonVerify.visibility = View.INVISIBLE
                val phoneAuthCredential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId,
                    code
                )
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener {
                        progressBar.visibility = View.GONE
                        buttonVerify.visibility = View.VISIBLE
                        if (it.isSuccessful) {
                            val email = intent.getStringExtra("email")
                            val password = intent.getStringExtra("password")
                            val name = intent.getStringExtra("name")
                            val image = intent.getStringExtra("image")

                            signUp(name, email, password, image)

                            val intent = Intent(applicationContext, LoginActivity::class.java)
                            startActivity(intent)

                            //НАДО РЕАЛИЗОВАТЬ ДОБАВЛЕНИЕ В FIRESTORE DATABASE
                            //Предложение как сделать: в активити регестрации записывать в файл почту, имя, пароль и тд
                            //А тут уже их из файла взять и зарегестрировать пользователя
                            finish()
                            showToast("Your account added to the database, please logIn")
                        } else {
                            showToast("The verification code entered was invalid")
                        }
                    }
            }
        }
    }

    private fun assigningValueInputCode1UntilInputCode6() {
        inputCode1 = binding.inputCode1
        inputCode2 = binding.inputCode2
        inputCode3 = binding.inputCode3
        inputCode4 = binding.inputCode4
        inputCode5 = binding.inputCode5
        inputCode6 = binding.inputCode6
    }

    private fun signUp(name: String?, email: String?, password: String?, image: String?) {
        val database: FirebaseFirestore = FirebaseFirestore.getInstance()
        val user: HashMap<String, Any> = HashMap()
        user[Constants.KEY_NAME] = name.toString()
        user[Constants.KEY_EMAIL] = email.toString()
        user[Constants.KEY_PASSWORD] = password.toString()
        if (image != null) {
            user[Constants.KEY_IMAGE] = image.toString()
        }
        database.collection(Constants.KEY_COLLECTIONS_USERS)
            .add(user)
            .addOnSuccessListener {
                preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN, true)
                preferenceManager.putString(Constants.KEY_USER_ID, it.id)
                preferenceManager.putString(
                    Constants.KEY_NAME,
                    name.toString()
                )
                if (image != null) {
                    preferenceManager.putString(Constants.KEY_IMAGE, image)
                }
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK / Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun addTextChangedListenerForInputCodeX(inputCodeX: EditText, inputCodeXAdd1: EditText) {
        inputCodeX.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    inputCodeXAdd1.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupOTPInputs() {
        addTextChangedListenerForInputCodeX(inputCode1, inputCode2)
        addTextChangedListenerForInputCodeX(inputCode2, inputCode3)
        addTextChangedListenerForInputCodeX(inputCode3, inputCode4)
        addTextChangedListenerForInputCodeX(inputCode4, inputCode5)
        addTextChangedListenerForInputCodeX(inputCode5, inputCode6)
    }


}