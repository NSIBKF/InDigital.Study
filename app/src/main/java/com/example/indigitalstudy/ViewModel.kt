package com.example.indigitalstudy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ViewModel : ViewModel() {
    val boolParametr: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}