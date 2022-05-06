package com.example.indigitalstudy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.indigitalstudy.databinding.FragmentProfileBinding
import com.example.indigitalstudy.databinding.FragmentSettingsBinding

class SettingsActivity: AppCompatActivity()  {
    lateinit var bindingClassSett: FragmentSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingClassSett = FragmentSettingsBinding.inflate(layoutInflater)
        setContentView(bindingClassSett.root)
    }

}