package com.example.indigitalstudy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.indigitalstudy.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var bindingClass : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClass = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(bindingClass.root)
        setListeners()
    }

    private fun setListeners() {
        bindingClass.signInText.setOnClickListener{
            onBackPressed()
        }
    }
}